package me.purnachandra;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;

import me.purnachandra.crawler.Crawler;
import me.purnachandra.crawler.UrlProcessor;
import me.purnachandra.db.UrlRepository;

public class Main {
    public static void main(String[] args) {

        String startUrl = "https://purnachandra.me";
        UrlRepository repo = new UrlRepository();
        repo.addUrl(startUrl);

        while (true) {
            Optional<String> nextUrlOpt = repo.getNextPendingUrl();

            if (nextUrlOpt.isEmpty()) {
                System.out.println("No more URLs to crawl");
                break;
            }

            String url = nextUrlOpt.get();
            String processedUrl = UrlProcessor.process(url);
            System.out.println("Crawling: " + url);

            Document document = null;

            try {
                document = Crawler.getDocument(processedUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (document != null) {
                List<String> obtainedUrl = Crawler.extractLinks(document);
                String title = Crawler.getTitle(document);
                System.out.println("Title: " + title);
                String description = Crawler.getDescription(document);
                System.out.println("Description: " + description);

                for (String u : obtainedUrl) {
                    String cleanedUrl = UrlProcessor.process(u);
                    repo.addUrl(cleanedUrl);
                }

                repo.addMetadata(processedUrl, title, description);
            }

            repo.markVisited(processedUrl);
        }
    }
}
