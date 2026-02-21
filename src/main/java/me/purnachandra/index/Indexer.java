package me.purnachandra.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Indexer {
    private static final Pattern TOKEN = Pattern.compile("\\W+");
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as",
            "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't",
            "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down",
            "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't",
            "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
            "himself", "his", "how", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's",
            "its", "itself", "just", "ll", "ma", "me", "mightn't", "more", "most", "mustn't", "my", "myself", "needn't",
            "no",
            "nor", "not", "now", "o'clock", "of", "off", "on", "once", "only", "or", "other", "our", "ours",
            "ourselves",
            "out", "over", "own", "re", "s", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't",
            "so", "some", "such", "t", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
            "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
            "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn't", "we",
            "we'd", "we'll", "we're", "we've", "were", "weren't",
            "what",
            "when",
            "where",
            "which",
            "while",
            "who",
            "whom",
            "why",
            "will",
            "with");

    public static void indexDocument(int docId, String text) {
        if (text == null || text.isEmpty())
            return;

        String[] tokens = tokenize(text);
        Map<String, Integer> freqs = new HashMap<>();

        for (String t : tokens) {
            if (t.isBlank() || t.length() <= 2 || STOP_WORDS.contains(t))
                continue;

            freqs.merge(t, 1, Integer::sum);

        }

        IndexRepository.addIndex(docId, freqs);
    }

    public static String[] tokenize(String text) {
        return TOKEN.split(text.toLowerCase().trim());
    }
}
