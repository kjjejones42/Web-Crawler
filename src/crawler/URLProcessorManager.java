package crawler;

import java.util.*;
import java.net.*;
import javax.swing.SwingWorker;
import java.util.concurrent.*;

class URLProcessorManager extends SwingWorker<List<URL>, Void> {


    private final WebCrawlerLogic webCrawler;
    private final Queue<URL> urlQueue;
    private final Set<URL> doneUrls;
    private final List<Future<?>> futures;
    private final int maxDepth;
    private final long endTime;
    private final ExecutorService executor;
    
    private int depth = 0;

    URLProcessorManager(URL rootUrl, WebCrawlerLogic webCrawler, int maxDepth, int workers, long endTime) {
        this.endTime = endTime;
        this.webCrawler = webCrawler;
        this.maxDepth = maxDepth;
        this.doneUrls = new LinkedHashSet<>();
        this.futures = new ArrayList<>();

        this.urlQueue = new LinkedList<>();
        urlQueue.add(rootUrl);
        executor = Executors.newFixedThreadPool(workers);
    }
    
    private synchronized boolean areFuturesRunning() {
        return futures.stream().anyMatch(i -> !i.isDone());
    }

    private synchronized boolean isStillRunning() {
        boolean a = areFuturesRunning();
        boolean b = isCancelled();
        boolean c = depth > maxDepth;
        boolean d = System.currentTimeMillis() > endTime;
        return a && !(b || c || d);
    }
    
    synchronized void addUrlToQueue(URL url, int depth) {
        if (!doneUrls.contains(url) && depth <= maxDepth) {
            this.depth = Math.max(depth, this.depth);
            urlQueue.add(url);
        }
    }

    synchronized URL getUrlFromQueue() {
        URL url = this.urlQueue.poll();
        if (url != null) {
            doneUrls.add(url);
        }
        return url;
    }

    @Override
    protected List<URL> doInBackground() throws Exception {
        URL url;
        do {
            if ((url = getUrlFromQueue()) != null) {
                Future<?> future = executor.submit(new URLProcessor(url, this, depth));
                futures.add(future);
            }
        } while (isStillRunning());
        return new ArrayList<>(doneUrls);
    }
    
    @Override
    protected void done() {  ;
        executor.shutdown();
    }
}