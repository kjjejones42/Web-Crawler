package crawler;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final JTextArea textArea;
    private final JScrollPane textAreaScrollPane;
    private final JTextField urlTextField;
    private final JButton runButton;
    private final JLabel titleLabel;

    private String getTextFromURL(String input) {
        final String siteText;
        try {
            final InputStream inputStream = new URL(input).openStream();
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
        textArea.setText(text);
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
        textArea.setEnabled(false);
        textArea.setLineWrap(true);

        textAreaScrollPane = new JScrollPane(textArea);

        urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");

        runButton = new JButton("Get Text!");
        runButton.setName("RunButton");
        runButton.addActionListener(e ->
            setText(getTextFromURL(urlTextField.getText()))
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