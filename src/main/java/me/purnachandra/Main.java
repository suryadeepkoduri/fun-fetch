package me.purnachandra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    public static void main(String[] args) {

        String startUrl = "https://purnachandra.me";
        Queue<String> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();
        queue.offer(startUrl);

        while (!queue.isEmpty()) {
            String url = queue.poll();
            System.out.println(url);
            List<String> obtainedUrl = LinkExtractor.extractLinks(url);

            for(String u:obtainedUrl) {
                if(!visited.contains(u)) {
                    visited.add(u);
                    queue.add(u);
                }
            }
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