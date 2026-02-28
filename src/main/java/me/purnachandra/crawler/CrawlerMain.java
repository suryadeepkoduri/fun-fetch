package me.purnachandra.crawler;

import java.util.List;

import me.purnachandra.crawler.repository.CrawlRepository;

public class CrawlerMain {
    public static void main(String[] args) {
        CrawlRepository crawlRepository = new CrawlRepository();
        PageFetcher pageFetcher = new PageFetcher(1000, 5000);
        PageParser pageParser = new PageParser();

        CrawlerOrchestrator orchestrator = new CrawlerOrchestrator(crawlRepository, pageFetcher, pageParser, 3, 100);
        orchestrator.start(List.of("https://news.ycombinator.com",
                "https://news.google.com", "https://www.reuters.com", "https://curlie.org", "https://www.w3.org",
                "https://arxiv.org"));
    }
}
