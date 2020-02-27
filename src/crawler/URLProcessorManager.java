package crawler;

import java.util.*;
import java.net.*;
import javax.swing.SwingWorker;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class URLProcessorManager extends SwingWorker<Void, Void> {

    static final long NO_TIME_LIMIT = 0;
    static final int SAME_PAGE_ONLY = 0;

    private final WebCrawlerLogic webCrawler;
    private final Queue<URLResult> urlQueue;
    private final Set<URL> foundUrls;
    private final List<Future<?>> futures;
    private final int maxDepth;
    private final long endTime;
    private final ExecutorService executor;
    
    private volatile int parsedURLs = 0;

    URLProcessorManager(URL rootUrl, WebCrawlerLogic webCrawler, int maxDepth, int workers, long maxTime) {
        this.endTime = maxTime == NO_TIME_LIMIT ? Long.MAX_VALUE : System.currentTimeMillis() + maxTime;
        this.webCrawler = webCrawler;
        this.maxDepth = maxDepth;
        this.foundUrls = ConcurrentHashMap.newKeySet();
        this.futures = new ArrayList<>();

        this.urlQueue = new ConcurrentLinkedQueue<>();
        addUrlToQueue(new URLResult(rootUrl, 0));
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
        boolean c = System.currentTimeMillis() > endTime;
        return a && !(b || c );
    }
    
    void addUrlToQueue(URLResult result) {
        if (result.url != null && !foundUrls.contains(result.url)) {
            foundUrls.add(result.url);
            if (result.depth <= maxDepth) {                
                urlQueue.add(result);
            }
        }
    }

    void incrementParsedURLs(URL url) {
        parsedURLs++;
        updateParsedUrls();
    }

    void updateParsedUrls(){
        webCrawler.updateCount(parsedURLs);     
    }

    URLResult getUrlFromQueue() {
        return this.urlQueue.poll();
    }

    @Override
    protected Void doInBackground() throws Exception {
        URLResult urlResult;
        do {
            if ((urlResult = getUrlFromQueue()) != null) {
                Future<?> future = executor.submit(new URLProcessor(urlResult, this));
                addFuture(future);
            }
        } while (isStillRunning());
        return null;
    }
    
    @Override
    protected void done() { 
        cancelFutures();
        executor.shutdown();
        List<String> result = foundUrls.stream().map(URL::toString).sorted().collect(Collectors.toList());      
        webCrawler.setUrls(result);
        updateParsedUrls();
    }
}