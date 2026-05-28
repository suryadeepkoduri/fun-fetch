package io.github.suryadeepkoduri.search.model;

public record SearchResult(int docId, String url, String title, double score) {}
