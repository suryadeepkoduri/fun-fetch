package io.github.suryadeepkoduri.search.query;

import io.github.suryadeepkoduri.search.Tokenizer;
import io.github.suryadeepkoduri.search.query.model.ProcessedQuery;

/**
 * DefaultQueryProcessor
 */
public class DefaultQueryProcessor implements QueryProcessor {

    private final Tokenizer tokenizer;

    public DefaultQueryProcessor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public ProcessedQuery process(String rawQuery) {
        return new ProcessedQuery(rawQuery, tokenizer.tokenize(rawQuery));
    }
}
