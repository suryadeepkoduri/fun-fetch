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

        // String startUrl = "https://purnachandra.me";
        String[] seedUrls = { "https://en.wikipedia.org/wiki/Main_Page", "https://dmoztools.net/",
                "https://www.bbc.com", "https://www.reuters.com", "https://stackoverflow.com", "https://medium.com",
                "https://developer.mozilla.org" };
        UrlRepository repo = new UrlRepository();
        // repo.addUrl(startUrl);

        for (String url : seedUrls) {
            repo.addUrl(url);
        }

        while (true) {
            Optional<String> nextUrlOpt = repo.getNextPendingUrl();

            if (nextUrlOpt.isEmpty()) {
                System.out.println("No more URLs to crawl");
                break;
            }

            String url = nextUrlOpt.get();
            String processedUrl = UrlProcessor.process(url);
            if (processedUrl == null)
                continue;
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
                String description = Crawler.getDescription(document);
                String content = Crawler.getContent(document);

                for (String u : obtainedUrl) {
                    String cleanedUrl = UrlProcessor.process(u);

                    if (cleanedUrl == null)
                        continue;

                    repo.addUrl(cleanedUrl);
                }

                repo.addData(processedUrl, title, description, content);
            }

            repo.markVisited(processedUrl);
        }
    }
}
