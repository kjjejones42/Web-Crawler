package crawler;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    private final WebCrawlerLogic webCrawler;

    private final JTextField urlTextField;
    private final JToggleButton runButton;
    private final JTextField workersTextField;
    private final JTextField depthTextField;
    private final JCheckBox depthCheckBox;
    private final JTextField timeTextField;
    private final JCheckBox timeCheckBox;
    private final JLabel timeLabel;
    private final JLabel parsedLabel;
    private final JTextField exportUrlTextField;
    private final JButton exportButton;
    private final List<JComponent> editables;

    private Thread incrementor;
    private int maxDepth; 
    private int workers; 
    private long maxTime;

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
        panel.add(workersTextField, c);
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

    private void processRunButtonOn() {
        try {
            if (!validateInputs()){
                throw new RuntimeException("The input values could not be parsed.");
            }            
            webCrawler.processUrlFromUser(urlTextField.getText(), maxDepth, workers, maxTime);     
            resetLabelFields();
            startTimer();
        } catch (Exception e) {
            displayError("Error: " + e.getCause().getMessage());
        }
    }

    private void startTimer() {        
        incrementor = new Thread(new Incrementor(System.currentTimeMillis(), timeLabel));
        incrementor.start();
    }

    private void stopTimer() {
        if (incrementor != null){
            incrementor.interrupt();
        }
    }

    private void processRunButtonOff(){      
        webCrawler.cancelJob();
        stopTimer();
        enableInput();
    }

    private boolean validateInputs() {
        try {            
            this.workers = Integer.parseInt(workersTextField.getText());              
            this.maxDepth = Integer.parseInt(depthTextField.getText());               
            this.maxTime = Double.valueOf(1000.0d * Double.parseDouble(timeTextField.getText())).longValue();
            return true;
        } catch (NumberFormatException | ClassCastException e) {   
            return false;
        }
    }

    private void setChildComponentProperties() {
        runButton.addItemListener(e -> {
            if(e.getStateChange() == 1)
                processRunButtonOn();
            if(e.getStateChange() == 2)
                processRunButtonOff();
        });
        exportButton.addActionListener(e -> 
            webCrawler.saveToFile(exportUrlTextField.getText())
        );
    }

    private void resetLabelFields() {
        this.parsedLabel.setText("0");
        this.timeLabel.setText("00:00");
    }

    synchronized void displayResults(List<String> urls) {
        stopTimer();
        JOptionPane.showMessageDialog(this, String.join("\n", urls), "InfoBox: ", JOptionPane.PLAIN_MESSAGE);
    }
    synchronized void displayError(String message) {
        stopTimer();
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.ERROR_MESSAGE);
    }

    void disableInput() {
        for (JComponent c : editables) {
            c.setEnabled(false);
        }
    }

    void enableInput() {
        for (JComponent c : editables) {
            c.setEnabled(true);
        }
    }

    void updateCount(int count) {
        SwingUtilities.invokeLater(() -> parsedLabel.setText(Integer.toString(count)));
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
        this.workersTextField = new JTextField("10");
        this.depthTextField = new JTextField("1");
        this.depthCheckBox = new JCheckBox("Enabled", true);
        this.timeTextField = new JTextField("60.00");
        this.timeCheckBox = new JCheckBox("Enabled", true);
        this.timeLabel = new JLabel();
        this.parsedLabel = new JLabel();
        this.exportUrlTextField = new JTextField(System.getProperty("user.home") +  System.getProperty("file.separator") + "results.txt");
        this.exportButton = new JButton("Save");

        this.editables = List.of(urlTextField, workersTextField, depthTextField, depthCheckBox, timeTextField, timeCheckBox, exportUrlTextField, exportButton);

        setChildComponentNames();
        setChildComponentProperties();
        addChildComponents();
        resetLabelFields();

        setVisible(true);
    }

    public static void main(String[] args) {
        new WebCrawler();
    }

}