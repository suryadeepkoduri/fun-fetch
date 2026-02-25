package me.purnachandra.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.purnachandra.db.Database;
import me.purnachandra.search.SearchResult;
import me.purnachandra.search.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {

    @GetMapping
    public List<SearchResult> search(@RequestParam String q, @RequestParam(defaultValue = "10") int limit) throws SQLException {
        Connection connection = Database.getConnection();
        SearchService searchService = new SearchService(connection);
        return searchService.search(q, limit);  
    }
}
