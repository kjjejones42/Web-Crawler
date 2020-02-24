package crawler;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.table.TableModel;

class WebCrawlerGUI extends JFrame {
    
    static final long serialVersionUID = 1;

    private final JScrollPane tableScrollPane;
    private final JTextField urlTextField;
    private final JButton runButton;
    private final JLabel titleLabel;
    private final JTable titlesTable;
    
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

    void setTitleLabel(String title) {        
        titleLabel.setText(title);
    }

    void setTableModel(TableModel dataModel) {
        titlesTable.setModel(dataModel);
    }

    WebCrawlerGUI(WebCrawler webCrawler) {        
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
            webCrawler.setUrl(urlTextField.getText());
            webCrawler.processUrlFromUser();
        });

        titleLabel = new JLabel();
        titleLabel.setName("TitleLabel");

        addChildComponents();
        
        setVisible(true);
    }

}