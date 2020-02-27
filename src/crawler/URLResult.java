package crawler;

import java.net.URL;

class URLResult {
    final URL url;
    final int depth;

    URLResult(URL url, int depth) {
        this.url = url;
        this.depth = depth;
    }
}