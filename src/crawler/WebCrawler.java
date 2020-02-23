package crawler;

import javax.swing.*;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);

        JTextArea textarea = new JTextArea();
        textarea.setName("TextArea");
        textarea.setText("HTML code?");
        textarea.setEnabled(false);
        add(textarea);

        setTitle("Window");

        setVisible(true);
    }

    public static void main(final String[] args) {
        new WebCrawler();
    }
}