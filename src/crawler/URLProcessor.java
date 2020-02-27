package crawler;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import java.nio.charset.StandardCharsets;
import java.net.*;

class URLProcessor implements Runnable {

    final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final URL rootUrl;
    private final URLProcessorManager executor;
    private final int depth;

    URLProcessor(URLResult firstUrl, URLProcessorManager manager){
        this.rootUrl = firstUrl.url;
        this.executor = manager;
        this.depth = firstUrl.depth;
    }
    
    private List<URL> hrefsToURLs(List<String> input) {
        return input.stream()
            .map(href -> hrefToURL(rootUrl, href))
            .distinct()
            .filter(Objects::nonNull)
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
        results.add(rootUrl.toString());
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
        try {
            String html = getHTMLFromURL(rootUrl);
            List<String> hrefs = getHrefsFromHTML(html);
            List<URL> urls = hrefsToURLs(hrefs);
            for (URL url : urls){
                executor.addUrlToQueue(new URLResult(url, depth + 1));
            }                      
        } catch (Exception e) {
            System.err.println(rootUrl.toString() + " | " + e.getMessage());;
        } finally {            
            executor.incrementParsedURLs(rootUrl);  
        }
    }
}