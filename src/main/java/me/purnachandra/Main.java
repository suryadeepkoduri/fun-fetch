package me.purnachandra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
            System.out.println("Crawling: "+url);

            List<String> obtainedUrl = LinkExtractor.extractLinks(url);

            for(String u:obtainedUrl) {
                repo.addUrl(u);
            }

            repo.markVisited(url);
        }
    }
}

class LinkExtractor {
    public static List<String> extractLinks(String url) {
        List<String> finalLinks = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");

            for(Element link:links) {
                String href = link.attr("abs:href");
                finalLinks.add(href);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return finalLinks;
    }
}