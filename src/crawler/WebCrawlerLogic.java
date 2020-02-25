package crawler;

import java.io.*;
import java.nio.file.*;
import javax.swing.table.*;

class WebCrawlerLogic {

    private final WebCrawler gui;
    private ProcessUrlWorker worker;

    WebCrawler getGUI() {
        return gui;
    }

    void processUrlFromUser(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        gui.setLoadingState();
        if (this.worker != null) {
            worker.cancel(true);
        }
        this.worker = new ProcessUrlWorker(text, this);
        worker.execute();
    }

    void saveToFile(TableModel data, String fileName) {
        if (data == null || fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName));
            for (int row = 0; row < data.getRowCount(); row++) {
                for (int col = 0; col < data.getColumnCount(); col++) {
                    String line = (String) data.getValueAt(row, col);
                    bw.write(line + System.lineSeparator());
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    WebCrawlerLogic(WebCrawler gui) {
        this.gui = gui;
    }
}