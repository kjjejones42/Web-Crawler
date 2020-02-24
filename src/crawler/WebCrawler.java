package crawler;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import javax.swing.table.*;

public class WebCrawler {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final WebCrawlerGUI gui;

    private URL url;

    private List<String> getAllHrefs(String input) {
        List<String> results = new ArrayList<>();
        results.add(url.toString());
        Matcher m = Pattern.compile("(?<=href=['\"]?)[^\\s'\">]+").matcher(input);
        while (m.find()) {
            results.add(m.group());
        }
        return results;
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

    private boolean isUrlHTML(URL url) {
        try {
            String contentType = url.openConnection().getContentType();
            if (contentType == null){
                return false;
            }
            return contentType.contains("text/html");
        } catch (IOException e) {
            System.err.println(url.toString());
            e.printStackTrace();
            return false;
        }
    }

    private String[] urlToTableRow(URL url) {
        String title;
        try {
            title = getTitleFromHTML(getTextFromURL(url));         
        } catch (RuntimeException e) {
            title = e.getMessage();
        }
        return new String[] { url.toString(), title };
    }

    private List<String[]> formatListOfHrefs(List<String> input) {
        return input.parallelStream()
            .map(href -> relURLToAbsURL(url, href))
            .filter(Objects::nonNull)
            .distinct()
            .filter(this::isUrlHTML)
            .map(this::urlToTableRow)
            .collect(Collectors.toList());
    }

    private DefaultTableModel HTMLToTable(String html) {        
        String[] columnNames = { "URL", "Title" };
        Object[] resultRows = formatListOfHrefs(getAllHrefs(html)).toArray();
        String[][] data = new String[resultRows.length][];
        for (int i = 0; i < resultRows.length; i++) {
            data[i] = (String[]) resultRows[i];
        }
        return new DefaultTableModel(data, columnNames);
    }

    private String getTextFromURL(URL url) {
        String siteText = "";
        try {
            final InputStream inputStream = url.openStream();
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final StringBuilder stringBuilder = new StringBuilder();

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                stringBuilder.append(nextLine);
                stringBuilder.append(LINE_SEPARATOR);
            }
            reader.close();
            siteText = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not load page.");
        }
        return siteText;
    }

    private String getTitleFromHTML(String text) {
        String title = "";
        try {
            Matcher m = Pattern.compile("(?<=<title>).*?(?=</title>)").matcher(text);
            if (m.find()) {
                title = m.group();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return title;
    }
    
    void processUrlFromUser(String text) {
        if (text == null || text.isEmpty()){
            return;
        }
        try {            
            url = new URL(text);            
            String html = getTextFromURL(url);
            gui.setTableModel(HTMLToTable(html));   
            gui.setTitleLabel(getTitleFromHTML(html));
        } catch (MalformedURLException e) {            
            gui.setTitleLabel("Invalid URL: " + e.getMessage());        
        } catch (RuntimeException e) {
            gui.setTitleLabel(e.getMessage());
        }
    }

    void saveToFile(TableModel data, String fileName) {
        if (data == null || fileName == null || fileName.isEmpty()){
            return;
        }
        try {            
            BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName));
            for (int row = 0; row < data.getRowCount(); row++) {
                for (int col = 0; col < data.getColumnCount(); col++) {
                    String line = (String) data.getValueAt(row, col);
                    bw.write(line + System.lineSeparator());
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebCrawler() {
        this.gui = new WebCrawlerGUI(this);
    }

    public static void main(final String[] args) {
        new WebCrawler();
    }
}