package me.purnachandra;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler {
    public static String getTitle(Document document) {
        String title = document.getElementsByTag("title").text();
        if (!title.isEmpty())
            return title;

        title = document.getElementsByTag("h1").text();
        if (!title.isEmpty())
            return title;

        return "";
    }

    public static String getDescription(Document document) {
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
}
