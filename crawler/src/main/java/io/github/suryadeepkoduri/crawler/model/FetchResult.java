package io.github.suryadeepkoduri.crawler.model;

import org.jsoup.nodes.Document;

public record FetchResult(
        String url,
        Document document,
        boolean success,
        String errorMessage) {

}
