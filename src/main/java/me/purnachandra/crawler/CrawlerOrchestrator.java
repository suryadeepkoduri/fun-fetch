package me.purnachandra.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.purnachandra.crawler.model.CrawlJob;
import me.purnachandra.crawler.model.FetchResult;
import me.purnachandra.crawler.model.ParsedPage;
import me.purnachandra.crawler.repository.CrawlRepository;

public class CrawlerOrchestrator {
    private final CrawlRepository crawlRepository;
    private final PageFetcher pageFetcher;
    private final PageParser pageParser;
    private final int batchSize;
    private final int maxDepth;

    private final Logger log = LoggerFactory.getLogger(CrawlerOrchestrator.class);

    public CrawlerOrchestrator(CrawlRepository crawlRepository, PageFetcher pageFetcher, PageParser pageParser,
            int maxDepth, int batchSize) {
        this.crawlRepository = crawlRepository;
        this.pageFetcher = pageFetcher;
        this.pageParser = pageParser;
        this.batchSize = batchSize;
        this.maxDepth = maxDepth;
    }    

    public void start(List<String> seedUrls) {
        crawlRepository.batchInsertUrls(seedUrls, 0);

        Queue<CrawlJob> localQueue = new LinkedList<>();

        while (true) {
            if (localQueue.isEmpty()) {
                List<CrawlJob> batch = crawlRepository.getNextPendingBatch(batchSize);

                if (batch.isEmpty()) {
                    log.info("Crawl Complete");
                    break;
                }

                localQueue.addAll(batch);
            }

            CrawlJob job = localQueue.poll();
            process(job);
        }
    }

    private void process(CrawlJob job) {
        FetchResult result = pageFetcher.fetch(job.url());

        if (!result.success()) {
            crawlRepository.markFailed(job.pageId());
            return;
        }

        ParsedPage page = pageParser.parse(result.url(), result.document());
        crawlRepository.saveCrawlContent(job.pageId(), page);
        // TODO: add indexing here

        if (job.depth() < maxDepth) {
            processDiscoveredLinks(job, page.outgoingLinks());
        }
    }

    private void processDiscoveredLinks(CrawlJob job, List<String> rawLinks) {
        List<String> cleanLinks = rawLinks.stream()
                .map(UrlProcessor::process)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (cleanLinks.isEmpty()) {
            return;
        }

        int childDepth = job.depth() + 1;

        Map<String, Integer> newIds = crawlRepository.batchInsertUrls(cleanLinks, childDepth);

        List<String> alreadyExisted = cleanLinks.stream()
                .filter(url -> !newIds.containsKey(url))
                .toList();

        Map<String, Integer> existingIds = alreadyExisted.isEmpty() ? Map.of()
                : crawlRepository.getIdsByUrls(alreadyExisted);
        
        Map<String,Integer> allIds = new HashMap<>();
        allIds.putAll(newIds);
        allIds.putAll(existingIds);

        List<Integer> toIds = new ArrayList<>(allIds.values());
        crawlRepository.batchInsertLinks(job.pageId(), toIds);
    }
}
