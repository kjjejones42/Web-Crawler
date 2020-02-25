package crawler;

import javax.swing.*;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    private final WebCrawlerLogic webCrawler;

    private final JTextField urlTextField;
    private final JToggleButton runButton;
    private final JTextField depthTextField;
    private final JCheckBox depthCheckBox;
    private final JTextField timeTextField;
    private final JCheckBox timeCheckBox;
    private final JLabel timeLabel;
    private final JLabel parsedLabel;
    private final JTextField exportUrlTextField;
    private final JButton exportButton;

    private void addChildComponents() {

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;

        c.gridy = 0;
        panel.add(new JLabel("Start URL: "), c);
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(urlTextField, c);
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(runButton, c);

        
        c.gridy = 1;
        panel.add(new JLabel("Workers: "), c);
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(new JTextField(), c);
        c.gridwidth = 1;
        c.weightx = 0;

        c.gridy = 2;
        panel.add(new JLabel("Max Depth: "), c);
        c.weightx = 1;        
        c.gridwidth = 2;
        panel.add(depthTextField, c);        
        c.gridwidth = 1;
        c.weightx = 0;
        panel.add(depthCheckBox, c);
        
        c.gridy = 3;
        panel.add(new JLabel("Time Limit: "), c);
        c.weightx = 1;
        panel.add(timeTextField, c);
        c.weightx = 0;
        panel.add(new JLabel("Seconds"), c);
        panel.add(timeCheckBox, c);
        
        c.gridy = 4;
        panel.add(new JLabel("Elapsed Time: "), c);
        panel.add(timeLabel, c);

        c.gridy = 5;
        panel.add(new JLabel("Parsed Pages: "), c);
        panel.add(parsedLabel, c);

        c.gridy = 6;
        panel.add(new JLabel("Export: "), c);
        c.weightx = 1;        
        c.gridwidth = 2;
        panel.add(exportUrlTextField, c);
        c.gridwidth = 1;
        c.weightx = 0;
        panel.add(exportButton, c);

        add(panel);
    }

    private void setChildComponentNames() {

        this.urlTextField.setName("UrlTextField;");
        this.runButton.setName("RunButton;");
        this.depthTextField.setName("DepthTextField;");
        this.depthCheckBox.setName("DepthCheckBox;");
        this.parsedLabel.setName("ParsedLabel;");
        this.exportUrlTextField.setName("ExportUrlTextField;");
        this.exportButton.setName("ExportButton;");
    }

    private void processRunButton(){        
        webCrawler.processUrlFromUser(
            urlTextField.getText(),
            2, //Integer.parseInt(depthTextField.getText()),
            10, //TODO
            Long.MAX_VALUE //Long.parseLong(timeTextField.getText())
        );
    }

    private void setChildComponentProperties() {
        runButton.addActionListener(e -> processRunButton());
        exportButton.addActionListener(e -> 
            webCrawler.saveToFile(exportUrlTextField.getText())
        );
    }

    synchronized void displayString(Object obj) {
        System.out.println(obj);
    }


    public WebCrawler() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 300);
        setLocationRelativeTo(null);
        setTitle("Web Crawler");

        this.webCrawler = new WebCrawlerLogic(this);
        this.urlTextField = new JTextField();
        this.runButton = new JToggleButton("Run");
        this.depthTextField = new JTextField();
        this.depthCheckBox = new JCheckBox("Enabled");
        this.timeTextField = new JTextField();
        this.timeCheckBox = new JCheckBox("Enabled");
        this.timeLabel = new JLabel("00:00");
        this.parsedLabel = new JLabel("0");
        this.exportUrlTextField = new JTextField();
        this.exportButton = new JButton("Save");

        setChildComponentNames();
        setChildComponentProperties();
        addChildComponents();

        setVisible(true);
    }

    public static void main(String[] args) {
        new WebCrawler();
    }

}