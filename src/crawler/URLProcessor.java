package crawler;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import java.nio.charset.StandardCharsets;
import java.net.*;

class URLProcessor implements Runnable {

    final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final URL firstUrl;
    private final URLProcessorManager manager;
    private final int depth;

    URLProcessor(URL firstUrl, URLProcessorManager manager) {
        this(firstUrl, manager, 0);
    }

    URLProcessor(URL firstUrl, URLProcessorManager manager, int depth){
        this.firstUrl = firstUrl;
        this.manager = manager;
        this.depth = depth;
    }
    
    private List<URL> hrefsToURLs(List<String> input) {
        return input.stream()
            .map(href -> hrefToURL(firstUrl, href))
            .distinct()
            .filter(Objects::nonNull)
            .filter(this::isUrlHTML)
            .collect(Collectors.toList());
    }

    private URL hrefToURL(URL base, String relUrl) {
        try {
            if (relUrl.startsWith("?"))
                relUrl = base.getPath() + relUrl;
            if (relUrl.indexOf('.') == 0 && base.getFile().indexOf('/') != 0) {
                base = new URL(base.getProtocol(), base.getHost(), base.getPort(), "/" + base.getFile());
            }
            return new URL(base, relUrl);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean isUrlHTML(URL url) {
        try {
            return isUrlHTML(getURLConnection(url));            
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isUrlHTML(URLConnection con) {
        String contentType = con.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.contains("text/html");
    }

    private String getHTMLFromURL(URL url) {
        try {
            final URLConnection con = getURLConnection(url);
            if (!isUrlHTML(con)){
                throw new RuntimeException("URL content type was not HTML");
            }
            final InputStream inputStream = con.getInputStream();
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final StringBuilder stringBuilder = new StringBuilder();

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                stringBuilder.append(nextLine);
                stringBuilder.append(LINE_SEPARATOR);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not load page.");
        }
    }

    private List<String> getHrefsFromHTML(String html) {
        List<String> results = new ArrayList<>();
        results.add(firstUrl.toString());
        Matcher m = Pattern.compile("(?<=href=['\"]?)[^\\s'\">]+").matcher(html);
        while (m.find()) {
            results.add(m.group());
        }
        return results;
    }

    private URLConnection getURLConnection(URL url) throws IOException {
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
        return con;
    }

    @Override
    public void run() {
        String html = getHTMLFromURL(firstUrl);
        List<String> hrefs = getHrefsFromHTML(html);
        List<URL> urls = hrefsToURLs(hrefs);
        for (URL url : urls){
            manager.addUrlToQueue(url, depth + 1);
        }            
    }
}