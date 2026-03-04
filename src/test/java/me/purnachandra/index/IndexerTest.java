package me.purnachandra.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexerTest {
    private Indexer indexer;
    
    @BeforeEach
    void setup() {
        indexer = new Indexer();
    }

    @Test
    void index_withEmptyString_returnsEmptyMap() {
        Map<String,Integer> result = indexer.index("");
        assertTrue(result.isEmpty());
    }

    @Test
    void index_withMixedCaseWords_countsAsSameTerm() {
        String content = "hello Hello hEllo heLlo helLo hellO HELLO";
        Map<String,Integer> result = indexer.index(content);

        assertEquals(1, result.size());
        assertEquals(7, result.get("hello"));
    }

    //TODO: add multiple test cases
    @Test
    void index_withStopWords_countsTermsWithoutStopWords() {
        String content = "Welcome to the world";
        Map<String,Integer> result = indexer.index(content);

        assertEquals(2, result.size());
    }
}
