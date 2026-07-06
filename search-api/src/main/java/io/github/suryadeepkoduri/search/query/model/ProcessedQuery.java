package io.github.suryadeepkoduri.search.query.model;

import java.util.List;

/**
 * ProcessedQuery
 */
public record ProcessedQuery(String raw, List<String> terms) {}
