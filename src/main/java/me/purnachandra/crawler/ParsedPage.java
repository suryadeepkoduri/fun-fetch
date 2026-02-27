package me.purnachandra.crawler;

import java.util.List;

public record ParsedPage(String url, String title, String description, String content, String contentHash,
        List<String> outgoingLinks) {

}