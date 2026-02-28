package me.purnachandra.crawler;

import org.jsoup.nodes.Document;

public record FetchResult(
        String url,
        Document document,
        boolean success,
        String errorMessage) {

}
