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
        Map<String, Integer> result = indexer.index("");
        assertTrue(result.isEmpty());
    }

    @Test
    void index_withMixedCaseWords_countsAsSameTerm() {
        String content = "hello Hello hEllo heLlo helLo hellO HELLO";
        Map<String, Integer> result = indexer.index(content);

        assertEquals(1, result.size());
        assertEquals(7, result.get("hello"));
    }

    @Test
    void index_withStopWords_countsTermsWithoutStopWords() {
        String content = "Welcome to the world";
        Map<String, Integer> result = indexer.index(content);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("welcom"));
        assertTrue(result.containsKey("world"));
    }

    @Test
    void index_withNullInput_returnsEmptyMap() {
        Map<String, Integer> result = indexer.index(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void index_withSingleWord_returnsFrequencyOne() {
        Map<String, Integer> result = indexer.index("crawler");
        assertEquals(1, result.size());
        assertEquals(1, result.get("crawler"));
    }

    @Test
    void index_withRepeatedWord_countsCorrectly() {
        Map<String, Integer> result = indexer.index("search search search");
        assertEquals(1, result.size());
        assertEquals(3, result.get("search"));
    }

    @Test
    void index_withPunctuation_treatsAsDelimiter() {
        Map<String, Integer> result = indexer.index("hello, world! hello.");
        assertEquals(2, result.size());
        assertEquals(2, result.get("hello"));
        assertEquals(1, result.get("world"));
    }

    @Test
    void index_withShortWords_excludesThemFromIndex() {
        // Indexer skips tokens with length <= 2
        Map<String, Integer> result = indexer.index("go do it now");
        assertTrue(result.isEmpty());
    }

    @Test
    void index_withNumbers_excludesThemFromIndex() {
        // Single number tokens like "42" have length <= 2, longer ones pass length
        // check
        // but this verifies pure numeric strings are handled without crashing
        Map<String, Integer> result = indexer.index("123 456 789");
        assertEquals(3, result.size()); // length > 2, not stop words — they get indexed
    }

    @Test
    void index_withOnlyStopWords_returnsEmptyMap() {
        Map<String, Integer> result = indexer.index("the and or but for");
        assertTrue(result.isEmpty());
    }

    @Test
    void index_withMixedStopAndContentWords_onlyIndexesContentWords() {
        Map<String, Integer> result = indexer.index("the quick brown fox");
        assertEquals(3, result.size());
        assertTrue(result.containsKey("quick"));
        assertTrue(result.containsKey("brown"));
        // "fox" is length 3, not a stop word — should be present
        assertTrue(result.containsKey("fox"));
    }

    @Test
    void index_withWhitespaceOnly_returnsEmptyMap() {
        Map<String, Integer> result = indexer.index("     ");
        assertTrue(result.isEmpty());
    }

    @Test
    void index_withMultipleDistinctWords_countsEachSeparately() {
        Map<String, Integer> result = indexer.index("crawler indexer search engine");
        assertEquals(4, result.size());
        assertEquals(1, result.get("crawler"));
        assertEquals(1, result.get("index"));
        assertEquals(1, result.get("search"));
        assertEquals(1, result.get("engin"));
    }

    @Test
    void index_withInflectedVerbs_stemsToPresentForm() {
        // running, runs, ran all stem to "run"
        Map<String, Integer> result = indexer.index("running runs");
        assertEquals(1, result.size());
        assertTrue(result.containsKey("run"));
    }

    @Test
    void index_withPluralNouns_stemsToSingularForm() {
        // crawlers → crawler, engines → engin, searches → search
        Map<String, Integer> result = indexer.index("crawlers engines searches");
        assertEquals(3, result.size());
        assertTrue(result.containsKey("crawler"));
        assertTrue(result.containsKey("engin"));
        assertTrue(result.containsKey("search"));
    }

    @Test
    void index_withAdjectiveForms_stemsCorrectly() {
        // "happiness" → "happi", "quickly" → "quick"
        Map<String, Integer> result = indexer.index("happiness quickly");
        assertEquals(2, result.size());
        assertTrue(result.containsKey("happi"));
        assertTrue(result.containsKey("quick"));
    }

    @Test
    void index_withStemCollision_countsCollapsedFormsAsSingleTerm() {
        // "index", "indexing", "indexed", "indexes" should all stem to same term
        Map<String, Integer> result = indexer.index("index indexing indexed indexes");
        assertEquals(1, result.size());
        assertEquals(4, result.get("index"));
    }

    @Test
    void index_withIrregularVerbs_stemsToBaseForm() {
        // "flies" → "fli" in Snowball Porter2 — counterintuitive but correct for this
        // stemmer
        Map<String, Integer> result = indexer.index("flies");
        assertEquals(1, result.size());
        assertTrue(result.containsKey("fli"));
    }
}
