package me.purnachandra.crawler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageParser {
    public ParsedPage parse(String url, Document document) {
        String title = getTitle(document);
        String description = getDescription(document);
        String content = getContent(document);
        List<String> links = extractLinks(document);
        String contentHash = sha256(content);
        return new ParsedPage(url, title, description, content, contentHash, links);    
    }

    private static String getTitle(Document document) {
        Element titleElement = document.selectFirst("title");
        if (titleElement != null && !titleElement.text().isEmpty())
            return titleElement.text();

        titleElement = document.selectFirst("h1");
        if (titleElement != null && !titleElement.text().isEmpty())
            return titleElement.text();

        return "";
    }

    private static String getDescription(Document document) {
        // various methods for picking description if default description not found
        String description = document.select("meta[name=description]").attr("content");
        if (!description.isEmpty())
            return description;

        description = document.select("meta[property=og:description]").attr("content");
        if (!description.isEmpty())
            return description;

        description = document.select("meta[name=twitter:description]").attr("content");
        if (!description.isEmpty())
            return description;

        Element p = document.selectFirst("p");
        if (p != null)
            return p.text();

        return "";
    }

    private static String getContent(Document document) {
        document.select("script,style,nav,footer,header,aside").remove();
        return document.body().text();
    }

    private static List<String> extractLinks(Document document) {
        List<String> finalLinks = new ArrayList<>();
        Elements links = document.select("a[href]");

        for (Element link : links) {
            String href = link.attr("abs:href");
            finalLinks.add(href);
        }

        return finalLinks;
    }

    private String sha256(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
