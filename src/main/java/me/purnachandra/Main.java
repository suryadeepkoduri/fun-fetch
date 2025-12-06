package me.purnachandra;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;

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
            System.out.println("Crawling: " + url);

            Document document = null;

            try {
                document = Crawler.getDocument(url);
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
                    repo.addUrl(u);
                }

                repo.addMetadata(url, title, description);
            }

            repo.markVisited(url);
        }
    }
}
