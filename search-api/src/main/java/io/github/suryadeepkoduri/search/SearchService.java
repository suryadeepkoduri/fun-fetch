package io.github.suryadeepkoduri.search;

import io.github.suryadeepkoduri.search.model.SearchResult;
import java.util.List;

public interface SearchService {
    List<SearchResult> search(String query, int limit);
}
