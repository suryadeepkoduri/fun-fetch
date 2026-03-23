package me.purnachandra.search.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import me.purnachandra.search.SearchResult;
import me.purnachandra.search.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    private static final int MAX_LIMIT = 100;

    @GetMapping
    public List<SearchResult> search(@RequestParam String q, @RequestParam(defaultValue = "10") int limit) {
        if (q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter 'q' must not be blank");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Parameter 'limit' must be between 1 and " + MAX_LIMIT);
        }
        return searchService.search(q, limit);
    }
}
