package io.github.suryadeepkoduri.search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.suryadeepkoduri.search.SearchResult;
import io.github.suryadeepkoduri.search.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchResult> search(@RequestParam String q, @RequestParam(defaultValue = "10") int limit) {
        return searchService.search(q, limit);
    }
}
