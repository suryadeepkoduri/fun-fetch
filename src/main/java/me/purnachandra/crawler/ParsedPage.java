package me.purnachandra.crawler;

import java.util.List;

public class ParsedPage {
    private String url;
    private String title;
    private String description;
    private String content;
    private String contentHash;

    private List<String> outgoingLinks;

    public ParsedPage(String url, String title, String description, String content, String contentHash, List<String> outgoingLinks) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.content = content;
        this.contentHash = contentHash;
        this.outgoingLinks = outgoingLinks;
    }

    public ParsedPage() {
    }

    public ParsedPage(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getOutgoingLinks() {
        return outgoingLinks;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOutgoingLinks(List<String> outgoingLinks) {
        this.outgoingLinks = outgoingLinks;
    }
}
