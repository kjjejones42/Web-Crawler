package crawler;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

import java.net.URL;

class WebCrawlerLogic {

    private final WebCrawler gui;
    private URLProcessorManager processor;

    WebCrawler getGUI() {
        return gui;
    }

    void processUrlFromUser(String text, int maxDepth, int workers, long maxTime) {
        if (text == null || text.isEmpty()) {
            return;
        }
        try {
            if (this.processor != null) {
                processor.cancel(true);
            }
            URL url = new URL(text);
            this.processor = new URLProcessorManager(url, this, maxDepth, workers, maxTime);
            processor.execute();
            SwingUtilities.invokeLater(() -> {                
                try {
                    getGUI().displayString(processor.get());                    
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveToFile(String fileName) {
        // if (data == null || fileName == null || fileName.isEmpty()) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName));
            // for (int row = 0; row < data.getRowCount(); row++) {
            //     for (int col = 0; col < data.getColumnCount(); col++) {
            //         String line = (String) data.getValueAt(row, col);
            //         bw.write(line + System.lineSeparator());
            //     }
            // }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    WebCrawlerLogic(WebCrawler gui) {
        this.gui = gui;
    }
}