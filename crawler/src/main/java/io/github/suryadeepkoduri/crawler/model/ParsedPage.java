package io.github.suryadeepkoduri.crawler.model;

import java.util.List;

public record ParsedPage(String url, String title, String description, String content, String contentHash,
        List<String> outgoingLinks) {

}