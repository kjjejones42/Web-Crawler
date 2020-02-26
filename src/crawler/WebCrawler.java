package crawler;

import java.util.*;
import javax.swing.*;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.concurrent.TimeUnit;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;
    static final int DEFAULT_DEPTH = 1;
    static final long DEFAULT_TIME = URLProcessorManager.NO_TIME_LIMIT;

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

    private URLProcessTimer incrementor;
    private int workers; 
    private int maxDepth = DEFAULT_DEPTH; 
    private long maxTime = DEFAULT_TIME;
    private boolean isDepthOn;
    private boolean isTimeOn;

    static String formatDuration(long millis) {
        long MINUTES = TimeUnit.MILLISECONDS.toMinutes(millis);
        return String.format("%02d:%02d", MINUTES,
            TimeUnit.MILLISECONDS.toSeconds(millis) - 
            TimeUnit.MINUTES.toSeconds(MINUTES));
    }

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
        urlTextField.setName("UrlTextField;");
        runButton.setName("RunButton;");
        depthTextField.setName("DepthTextField;");
        depthCheckBox.setName("DepthCheckBox;");
        parsedLabel.setName("ParsedLabel;");
        exportUrlTextField.setName("ExportUrlTextField;");
        exportButton.setName("ExportButton;");
    }
    

    private void processRunButtonOn() {
        try {
            if (!validateInputs()){
                throw new RuntimeException("The input values could not be parsed.");
            }            
            webCrawler.processUrlFromUser(
                urlTextField.getText(),
                isDepthOn ? maxDepth : DEFAULT_DEPTH,
                workers,
                isTimeOn ? maxTime : DEFAULT_TIME
            );     
            resetLabelFields();
            startTimer();
            disableInput();
        } catch (RuntimeException e) {            
            displayError("Error: " + e.getMessage());
        } catch (MalformedURLException e) {
            displayError("Error: Invalid URL: " + e.getMessage());
        }
    }

    private void processRunButtonOff(){      
        webCrawler.cancelJob();
        stopTimer();
        enableInput();
    }

    private void startTimer() {  
        stopTimer(); 
        incrementor = new URLProcessTimer(System.currentTimeMillis(), timeLabel);
        incrementor.run();
    }

    private void stopTimer() {
        if (incrementor != null){
            incrementor.cancel();
        }
    }

    private boolean validateInputs() {
        try {            
            workers = Integer.parseInt(workersTextField.getText());  
            if (isDepthOn) {
                maxDepth = Integer.parseInt(depthTextField.getText()); 
            }                      
            if (isTimeOn) {  
                maxTime = Double.valueOf(1000.0d * Double.parseDouble(timeTextField.getText())).longValue();
            }
            return true;
        } catch (NumberFormatException | NullPointerException e) {   
            return false;
        }
    }

    private void setDepthOn(boolean on) {
        isDepthOn = on;        
        depthTextField.setEnabled(on);
        depthCheckBox.setSelected(on);
        if (on) {
            depthTextField.setText(Integer.toString(maxDepth));
            editables.add(depthTextField);
        } else {
            depthTextField.setText(Integer.toString(DEFAULT_DEPTH));
            editables.remove(depthTextField);
        }
    }
    
    private void setTimeOn(boolean on) {
        isTimeOn = on;
        timeTextField.setEnabled(on);
        timeCheckBox.setSelected(on);
        if (on) {
            timeTextField.setText(Long.toString(maxTime / 1000));
            editables.add(timeTextField);
        } else {
            timeTextField.setText("No time limit set.");
            editables.remove(timeTextField);
        }
    }

    private void setChildComponentProperties() {

        runButton.addActionListener(e -> {
            if (runButton.isSelected()) {
                processRunButtonOn();
            } else {
                processRunButtonOff();
            }
        });

        exportButton.addActionListener(e -> 
            webCrawler.saveToFile(exportUrlTextField.getText())
        );

        depthCheckBox.addActionListener(e -> setDepthOn(depthCheckBox.isSelected()));

        timeCheckBox.addActionListener(e -> setTimeOn(timeCheckBox.isSelected()));
    }

    private void resetLabelFields() {
        parsedLabel.setText("0");
        timeLabel.setText(formatDuration(0));
    }

    synchronized void displayResults(List<String> urls) {
        stopTimer();
        runButton.setSelected(false);
        JOptionPane.showMessageDialog(this, String.join("\n", urls), "InfoBox: ", JOptionPane.PLAIN_MESSAGE);
    }

    synchronized void displayError(String message) {
        stopTimer();
        runButton.setSelected(false);
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

        webCrawler = new WebCrawlerLogic(this);
        urlTextField = new JTextField();
        runButton = new JToggleButton("Run");
        workersTextField = new JTextField("10");
        depthTextField = new JTextField();
        depthCheckBox = new JCheckBox("Enabled");
        timeTextField = new JTextField();
        timeCheckBox = new JCheckBox("Enabled");
        timeLabel = new JLabel();
        parsedLabel = new JLabel();
        exportUrlTextField = new JTextField(System.getProperty("user.home") +  System.getProperty("file.separator") + "results.txt");
        exportButton = new JButton("Save");

        editables = new ArrayList<>(List.of(urlTextField, workersTextField, depthTextField, depthCheckBox, timeTextField, timeCheckBox, exportUrlTextField, exportButton));

        setChildComponentNames();
        setChildComponentProperties();
        addChildComponents();
        resetLabelFields();

        setDepthOn(true);
        setTimeOn(false);

        setVisible(true);
    }

    public static void main(String[] args) {
        new WebCrawler();
    }

}