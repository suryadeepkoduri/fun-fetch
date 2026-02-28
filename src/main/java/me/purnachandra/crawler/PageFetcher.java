package me.purnachandra.crawler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.purnachandra.crawler.model.FetchResult;

public class PageFetcher {
    private final Map<String, Long> lastFetchTime;
    private final long politenessDelayMs;
    private final int timeoutMs;

    private final Logger log = LoggerFactory.getLogger(PageFetcher.class);

    public PageFetcher(long politenessDelayMs, int timeoutMs) {
        this.politenessDelayMs = politenessDelayMs;
        this.timeoutMs = timeoutMs;
        lastFetchTime = new ConcurrentHashMap<>();
    }

    public FetchResult fetch(String url) {
        enforcePoliteness(extractDomain(url));
        long start = System.currentTimeMillis();

        try {
            Document document = Jsoup.connect(url)
                    .userAgent("FunFetch/1.0 (learning project)")
                    .timeout(timeoutMs)
                    .get();
            long elapsed = System.currentTimeMillis() - start;
            log.info("FETCH OK url:{} duration:{} ms", url, elapsed);
            return new FetchResult(url, document, true, null);
        } catch (IOException e) {
            long elapsed = System.currentTimeMillis()-start;
            log.warn("FETCH FAIL url:{} duration:{}ms error:{}",url,elapsed,e.getMessage());
            return new FetchResult(url, null, false, e.getMessage());
        }
    }

    private void enforcePoliteness(String domain) {
        long lastFetch = lastFetchTime.getOrDefault(domain, 0L);
        long waitNeeded = politenessDelayMs - (System.currentTimeMillis() - lastFetch);

        if (waitNeeded > 0) {
            try {
                Thread.sleep(waitNeeded);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        lastFetchTime.put(domain, System.currentTimeMillis());
    }

    private String extractDomain(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }
}
