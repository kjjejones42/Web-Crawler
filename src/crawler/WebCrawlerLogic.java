package crawler;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.net.*;

class WebCrawlerLogic {

    private final WebCrawler gui;
    private URLProcessorManager worker;
    private List<String> urls;

    void cancelJob() {        
        if (this.worker != null) {
            worker.cancel(true);
        }        
    }

    void setUrls(List<String> urls) {
        gui.enableInput();
        this.urls = urls;
        if (urls.isEmpty()) {
            gui.displayError("The initial URL is invalid.");
        } else {
            gui.displayResults(urls);
        }
    }

    void updateCount(int count) {
        gui.updateCount(count);
    }

    void processUrlFromUser(String text, int maxDepth, int workers, long maxTime) throws MalformedURLException {
        URL url = new URL(text);
        cancelJob();
        this.worker = new URLProcessorManager(url, this, maxDepth, workers, maxTime);
        worker.execute();
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