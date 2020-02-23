package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URL;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;
import java.util.*;
import java.util.stream.Collectors;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final JScrollPane tableScrollPane;
    private final JTextField urlTextField;
    private final JButton runButton;
    private final JLabel titleLabel;
    private final String[] columnNames = { "URL", "Title" };
    private final JTable titlesTable;

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

    private URL resolve(URL base, String relUrl) {        
        try {      
            if (relUrl.startsWith("?"))
                relUrl = base.getPath() + relUrl;
            if (relUrl.indexOf('.') == 0 && base.getFile().indexOf('/') != 0) {
                base = new URL(base.getProtocol(), base.getHost(), base.getPort(), "/" + base.getFile());
            }      
            return new URL(base, relUrl);            
        } catch (Exception e) {
            return null;
        }
    }

    private List<String[]> formatListOfHrefs(List<String> input) {
        return input.stream()
            .map(href -> resolve(url, href))
            .filter(Objects::nonNull)
            .distinct()
            .filter(url -> {
                try {
                    String contentType = url.openConnection().getContentType();
                    if (contentType == null){
                        return false;
                    }
                    return List.of(contentType.replaceAll("\\s", "").split(";")).contains("text/html");
                } catch (Exception e) {
                    System.err.println(url.toString());
                    e.printStackTrace();
                    return false;
                }
            })
            .map(url -> new String[] { url.toString(), getTitleFromHTML(getTextFromURL(url)) })
            .collect(Collectors.toList());
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
        } catch (Exception e) {
            System.err.printf("No content: %s\n", url.toString());
        }
        return siteText;
    }

    private void addChildComponents() {

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 0, 10);
        c.fill = GridBagConstraints.BOTH;

        c.gridy = 0;
        add(new JLabel("URL: "), c);

        c.weightx = 1;
        add(urlTextField, c);

        c.weightx = 0;
        add(runButton, c);

        c.gridy = 1;
        c.weightx = 0;
        add(new JLabel("Title: "), c);

        c.weightx = 1;
        add(titleLabel, c);

        c.gridy = 2;
        c.weighty = 1;
        c.gridwidth = 3;
        c.insets = new Insets(10, 10, 10, 10);
        add(tableScrollPane, c);

    }

    String getTitleFromHTML(String text) {
        String title = "";
        try {
            Pattern p = Pattern.compile("(?<=<title>).*?(?=</title>)");
            Matcher m = p.matcher(text);
            if (m.find()) {
                title = m.group();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    void setText(String text) {
        String title = getTitleFromHTML(text);
        titleLabel.setText(title);
        Object[] a = formatListOfHrefs(getAllHrefs(text)).toArray();
        String[][] data = new String[a.length][];
        for (int i = 0; i < a.length; i++) {
            data[i] = (String[]) a[i];
        }
        titlesTable.setModel(new DefaultTableModel(data, columnNames));
    }

    void setUrl(String text) {
        try {
            url = new URL(text);
        } catch (Exception e) {
            e.printStackTrace();
            url = null;
        }
    }

    public WebCrawler() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        setTitle("Web Crawler");

        titlesTable = new JTable();
        titlesTable.setName("TitlesTable");
        titlesTable.setEnabled(false);
        tableScrollPane = new JScrollPane(titlesTable);

        urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");

        runButton = new JButton("Get Text!");
        runButton.setName("RunButton");
        runButton.addActionListener(e -> {
            setUrl(urlTextField.getText());
            setText(getTextFromURL(url));
        });

        titleLabel = new JLabel();
        titleLabel.setName("TitleLabel");

        addChildComponents();

        setVisible(true);
    }

    public static void main(final String[] args) {
        new WebCrawler();
    }
}