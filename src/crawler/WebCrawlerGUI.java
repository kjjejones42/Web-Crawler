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

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;

        c.gridy = 0;
        panel.add(new JLabel("URL: "), c);

        c.weightx = 1;
        panel.add(urlTextField, c);

        c.weightx = 0;
        panel.add(runButton, c);

        c.gridy = 1;
        c.weightx = 0;
        panel.add(new JLabel("Title: "), c);

        c.weightx = 1;
        panel.add(titleLabel, c);

        c.gridy = 2;
        c.weighty = 1;
        c.gridwidth = 3;
        panel.add(tableScrollPane, c);

        add(panel);

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