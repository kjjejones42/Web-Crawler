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
    private final Set<URL> doneUrls;
    private final List<Future<?>> futures;
    private final int maxDepth;
    private final long endTime;
    private final ExecutorService executor;
    
    private volatile int depth = 0;

    URLProcessorManager(URL rootUrl, WebCrawlerLogic webCrawler, int maxDepth, int workers, long maxTime) {
        this.endTime = maxTime == NO_TIME_LIMIT ? Long.MAX_VALUE : System.currentTimeMillis() + maxTime;
        this.webCrawler = webCrawler;
        this.maxDepth = maxDepth;
        this.doneUrls = new HashSet<>();
        this.futures = new ArrayList<>();

        this.urlQueue = new ArrayDeque<>();
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
        if (url != null && !doneUrls.contains(url) && depth <= maxDepth) {
            this.depth = Math.max(depth, this.depth);
            urlQueue.add(url);
            doneUrls.add(url);
            webCrawler.updateCount(doneUrls.size());
        }
    }

    synchronized URL getUrlFromQueue() {
        URL url = this.urlQueue.poll();
        return url;
    }

    @Override
    protected Void doInBackground() throws Exception {
        URL url;
        do {
            if ((url = getUrlFromQueue()) != null) {
                Future<?> future = executor.submit(new URLProcessor(url, this, depth));
                futures.add(future);
            }
        } while (isStillRunning());
        return null;
    }
    
    @Override
    protected void done() { 
        futures.forEach(i -> i.cancel(true));
        List<String> result = doneUrls.stream().map(URL::toString).sorted().collect(Collectors.toList());      
        webCrawler.setUrls(result);
        executor.shutdown();
    }
}