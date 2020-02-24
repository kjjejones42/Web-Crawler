package crawler;

import javax.swing.*;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.table.TableModel;

public class WebCrawler extends JFrame {

    static final long serialVersionUID = 1;

    private final WebCrawlerLogic webCrawler;

    private final JScrollPane tableScrollPane;
    private final JTextField urlTextField;
    private final JButton runButton;
    private final JLabel titleLabel;
    private final JTable titlesTable;
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
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(tableScrollPane, c);

        c.gridy = 3;
        c.weighty = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(new JLabel("Export"), c);

        c.weightx = 1;
        panel.add(exportUrlTextField, c);

        c.weightx = 0;
        panel.add(exportButton, c);

        add(panel);
    }

    private void setChildComponentNames() {
        titlesTable.setName("TitlesTable");
        urlTextField.setName("UrlTextField");
        runButton.setName("RunButton");
        titleLabel.setName("TitleLabel");
        exportUrlTextField.setName("ExportUrlTextField");
        exportButton.setName("ExportButton");
    }

    private void setChildComponentProperties() {
        titlesTable.setEnabled(false);
        runButton.addActionListener(e -> webCrawler.processUrlFromUser(urlTextField.getText()));
        exportButton
                .addActionListener(e -> webCrawler.saveToFile(titlesTable.getModel(), exportUrlTextField.getText()));

    }

    void setTitleLabel(String title) {
        titleLabel.setText(title);
    }

    void setTableModel(TableModel dataModel) {
        titlesTable.setModel(dataModel);
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

        this.webCrawler = new WebCrawlerLogic(this);
        this.titlesTable = new JTable();
        this.tableScrollPane = new JScrollPane(titlesTable);
        this.urlTextField = new JTextField();
        this.runButton = new JButton("Get Text!");
        this.titleLabel = new JLabel();
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