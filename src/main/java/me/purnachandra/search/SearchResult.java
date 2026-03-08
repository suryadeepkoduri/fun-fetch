package me.purnachandra.search;

public record SearchResult(int docId, String url, String title, double score) {
}
