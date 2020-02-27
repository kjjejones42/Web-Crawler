package crawler;

import java.util.*;
import java.net.*;
import javax.swing.SwingWorker;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class URLProcessorManager extends SwingWorker<Void, Void> {

    static final long NO_TIME_LIMIT = 0;

    private final WebCrawlerLogic webCrawler;
    private final Queue<URL> urlQueue;
    private final Set<URL> processedUrls;
    private final List<Future<?>> futures;
    private final int maxDepth;
    private final long endTime;
    private final ExecutorService executor;
    
    private volatile int depth = 0;

    URLProcessorManager(URL rootUrl, WebCrawlerLogic webCrawler, int maxDepth, int workers, long maxTime) {
        this.endTime = maxTime == NO_TIME_LIMIT ? Long.MAX_VALUE : System.currentTimeMillis() + maxTime;
        this.webCrawler = webCrawler;
        this.maxDepth = maxDepth;
        this.processedUrls = ConcurrentHashMap.newKeySet();
        this.futures = new ArrayList<>();

        this.urlQueue = new ConcurrentLinkedQueue<>();
        urlQueue.add(rootUrl);
        executor = Executors.newFixedThreadPool(workers);
    }
    
    private boolean areFuturesRunning() {
        boolean result;
        synchronized (futures) {
            result = futures.stream().anyMatch(i -> !i.isDone());
        }
        return result;
    }

    private void addFuture(Future<?> future) {
        synchronized (futures) {
            futures.add(future);
        }
    }

    private void cancelFutures() {
        synchronized (futures) {            
            futures.forEach(i -> i.cancel(true));
        }
    }

    private boolean isStillRunning() {
        boolean a = areFuturesRunning();
        boolean b = isCancelled();
        boolean c = depth > maxDepth;
        boolean d = System.currentTimeMillis() > endTime;
        return a && !(b || c || d);
    }
    
    void addUrlToQueue(URL url, int depth) {
        if (url != null && !processedUrls.contains(url) && depth <= maxDepth) {
            this.depth = Math.max(depth, this.depth);
            urlQueue.add(url);
            processedUrls.add(url);
            webCrawler.updateCount(processedUrls.size());
        }
    }

    URL getUrlFromQueue() {
        URL url = this.urlQueue.poll();
        return url;
    }

    @Override
    protected Void doInBackground() throws Exception {
        URL url;
        do {
            if ((url = getUrlFromQueue()) != null) {
                Future<?> future = executor.submit(new URLProcessor(url, this, depth));
                addFuture(future);
            }
        } while (isStillRunning());
        return null;
    }
    
    @Override
    protected void done() { 
        cancelFutures();
        List<String> result = processedUrls.stream().map(URL::toString).sorted().collect(Collectors.toList());      
        webCrawler.setUrls(result);
        executor.shutdown();
    }
}