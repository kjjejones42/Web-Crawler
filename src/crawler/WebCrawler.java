package crawler;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String getTextFromURL(String input) {
        final String siteText;
        try {                
            final String url = input; 
            final InputStream inputStream = new URL(url).openStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

    public WebCrawler() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        
        setLayout(new GridBagLayout());        


        JTextArea textArea = new JTextArea();
        textArea.setName("HtmlTextArea");
        textArea.setText("HTML code?");
        textArea.setEnabled(false);

        JScrollPane textAreaScrollPane = new JScrollPane(textArea);

        JTextField urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");
        
        JButton runButton = new JButton("Get Text!");
        runButton.setName("runButton");
        runButton.addActionListener(e -> {
            textArea.setText(getTextFromURL(urlTextField.getText()));
        });

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,0,10);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridy = 0;

        add(urlTextField, c);

        c.weightx = 0;
        add(runButton, c);

        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.insets = new Insets(10,10,10,10);
        add(textAreaScrollPane, c);
        
        setTitle("Window");

        setVisible(true);
    }

    public static void main(final String[] args) {
        new WebCrawler();
    }
}