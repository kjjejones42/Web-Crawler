package crawler;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.net.*;
import javax.swing.SwingWorker;

class ProcessUrlWorker extends SwingWorker<Void,Void> {

    final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final WebCrawlerLogic parent;
    private final String text;
    private String html;
    private String title;
    
    private URL url;

    ProcessUrlWorker(String text, WebCrawlerLogic parent) {
        this.text = text;
        this.parent = parent;
    }
    
    // private DefaultTableModel HTMLToTable(String html) {
    //     String[] columnNames = { "URL", "Title" };
    //     Object[] resultRows = formatListOfHrefs(getAllHrefs(html)).toArray();
    //     String[][] data = new String[resultRows.length][];
    //     for (int i = 0; i < resultRows.length; i++) {
    //         data[i] = (String[]) resultRows[i];
    //     }
    //     return new DefaultTableModel(data, columnNames);
    // }
    // 
    // private String[] urlToTableRow(URL url) {
    //     String title;
    //     try {
    //         title = getTitleFromHTML(getTextFromURL(url));
    //     } catch (RuntimeException e) {
    //         title = e.getMessage();
    //     }
    //     return new String[] { url.toString(), title };
    // }

    private List<URL> formatListOfHrefs(List<String> input) {
        return input.parallelStream()
            .map(href -> relURLToAbsURL(url, href))
            .filter(Objects::nonNull)
            .distinct()
            .filter(this::isUrlHTML)
            // .map(this::urlToTableRow)
            .collect(Collectors.toList());
    }

    private URL relURLToAbsURL(URL base, String relUrl) {
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

    private String getHTMLFromURL(URL url) {
        try {
            final InputStream inputStream = getURLConnection(url).getInputStream();
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

    private String getTitleFromHTML(String html) {
        String title = "";
        try {
            Matcher m = Pattern.compile("(?<=<title>).*?(?=</title>)").matcher(html);
            if (m.find()) {
                title = m.group();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return title;
    }

    private List<String> getHrefsFromHTML(String html) {
        List<String> results = new ArrayList<>();
        results.add(url.toString());
        Matcher m = Pattern.compile("(?<=href=['\"]?)[^\\s'\">]+").matcher(html);
        while (m.find()) {
            results.add(m.group());
        }
        return results;
    }

    private URLConnection getURLConnection(URL url) throws IOException {
        if (isCancelled()) {
            return null;
        }
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
        return con;
    }

    private boolean isUrlHTML(URL url) {
        try {
            String contentType = getURLConnection(url).getContentType();
            if (contentType == null) {
                return false;
            }
            return contentType.contains("text/html");
        } catch (IOException e) {
            System.err.println(url.toString());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        // url = new URL(text);
        // html = getTextFromURL(url);
        // title = getTitleFromHTML(html);
        // return HTMLToTable(html);
        return null;
    }
    
    @Override
    protected void done() {  
        if (!isCancelled()) { 
            // WebCrawler gui = parent.getGUI();
            // try {
            //     TableModel model = get();
            //     gui.setTableModel(model);
            //     gui.setTitleLabel(title);
            // } catch (ExecutionException e) {
            //     gui.setTitleLabel(e.getCause().getMessage());
            // } catch (InterruptedException e) {
            //     gui.setTitleLabel(e.getMessage());
            // }
        }
    }
}