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
    private final JTable dataTable;

    private URL url;

    private List<String> getAllHrefs(String input) {
        List<String> results = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=href=['\"]?)[^\\s'\">]+").matcher(input);
        while (m.find()) {
            results.add(m.group());
        }
        return results;
    }

    private List<String[]> formatListOfHrefs(List<String> input) {
        String protocol = url.getProtocol();
        String start = protocol + "://" + url.getHost();
        List<String[]> result = new ArrayList<>();
        List<String> formatted = input.stream().map(href -> {
            if (href.startsWith("//")) {
                return protocol + "://" + href.substring(2);
            } else if (href.startsWith("/")) {
                return start + "/" + href.substring(1);
            } else if (href.startsWith("#")) {
                return start + href;
            } else {
                try {
                    new URL(href);
                    return href;
                } catch (Exception e) {
                    return null;
                }
            }
        }).filter(Objects::nonNull).distinct().filter(href -> {
            try {
                URL u = new URL(href);
                return List.of(u.openConnection().getContentType().replace("\\s", "").split(";")).contains("text/html");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).collect(Collectors.toList());
        for (String i : formatted) {
            try {
                result.add(new String[] { i, getTitleFromHTML(getTextFromURL(new URL(i))) });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
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
            System.err.println(url.getPath() + "\n" + e.getMessage());
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
        dataTable.setModel(new DefaultTableModel(data, columnNames));
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

        dataTable = new JTable();
        tableScrollPane = new JScrollPane(dataTable);

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