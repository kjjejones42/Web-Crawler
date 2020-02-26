package crawler;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.net.URL;

class WebCrawlerLogic {

    private final WebCrawler gui;
    private URLProcessorManager processor;
    private List<String> urls;

    void cancelJob() {        
        if (this.processor != null) {
            processor.cancel(true);
        }        
    }

    void setUrls(List<String> urls) {
        gui.enableInput();
        gui.displayResults(urls);
        this.urls = urls;
    }

    void updateCount(int count) {
        gui.updateCount(count);
    }

    WebCrawler getGUI() {
        return gui;
    }

    void processUrlFromUser(String text, int maxDepth, int workers, long maxTime) {
        if (text == null || text.isEmpty()) {
            return;
        }
        try {
            URL url = new URL(text);
            cancelJob();
            gui.disableInput();
            this.processor = new URLProcessorManager(url, this, maxDepth, workers, maxTime);
            processor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveToFile(String fileName) {
        if (urls == null || fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName));
            for (String url : urls) {
                bw.write(url + System.lineSeparator());
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