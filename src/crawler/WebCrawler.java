package crawler;

import javax.swing.*;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);
    }    

    public static void main(final String[] args) {
        new WebCrawler();
    }
}