package crawler;

import javax.swing.*;
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

    private final JTextArea textArea;
    private final JScrollPane textAreaScrollPane;
    private final JTextField urlTextField;
    private final JButton runButton;
    private final JLabel titleLabel;

    private URL url;

    private List<String> getAllHrefs(String input) {
        List<String> results = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=href=[\"\']).*?(?=[\"\'])").matcher(input);
        while (m.find()){
            results.add(m.group());
        }
        return results;
    }

    private List<String> formatListOfHrefs(List<String> input) {
        String protocol = this.url.getProtocol();
        String start = protocol + "://" + this.url.getHost();
        return input.stream()
        .map(href -> {
            if (href.startsWith("//")){
                return protocol + "://" + href.substring(2);
            } else if (href.startsWith("/")){
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
            }})
        .filter(x -> x != null)
        .collect(Collectors.toList());
    }

    private String getTextFromURL(URL url) {
        final String siteText;
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
            e.printStackTrace();
            return "ERROR\n" + e.getMessage();
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
        add(textAreaScrollPane, c);

    }

    void setText(String text) {
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
        titleLabel.setText(title);
        textArea.setText(String.join("\n\n",formatListOfHrefs(getAllHrefs(text))));
    }

    void setUrl(String text){
        try {
            this.url = new URL(text);            
        } catch (Exception e) {
            e.printStackTrace();
            this.url = null;
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

        textArea = new JTextArea();
        textArea.setName("HtmlTextArea");
        // textArea.setEnabled(false);
        textArea.setLineWrap(true);

        textAreaScrollPane = new JScrollPane(textArea);

        urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");

        runButton = new JButton("Get Text!");
        runButton.setName("RunButton");
        runButton.addActionListener(e -> {
            setUrl(urlTextField.getText());
            setText(getTextFromURL(this.url));
        }
        );

        titleLabel = new JLabel();
        titleLabel.setName("TitleLabel");

        addChildComponents();

        setVisible(true);
    }

    public static void main(final String[] args) {
        new WebCrawler();
    }
}