package io.github.suryadeepkoduri.search.query;

import io.github.suryadeepkoduri.search.query.model.ProcessedQuery;

/**
 * QueryProcessor
 */
public interface QueryProcessor {
    ProcessedQuery process(String rawQuery);
}
