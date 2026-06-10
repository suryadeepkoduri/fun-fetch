package io.github.suryadeepkoduri.search;

import java.util.List;

public abstract class Search {

    private final Tokenizer tokenizer;

    public Search(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public List<String> preprocessing(String query) {
        return tokenizer.tokenize(query);
    }
}
