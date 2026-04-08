package me.purnachandra.index;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.tartarus.snowball.ext.englishStemmer;

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
            "no", "nor", "not", "now", "o'clock", "of", "off", "on", "once", "only", "or", "other", "our", "ours",
            "ourselves", "out", "over", "own", "re", "s", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
            "shouldn't",
            "so", "some", "such", "t", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
            "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
            "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn't", "we",
            "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "when", "where", "which", "while", "who",
            "whom", "why", "will", "with");

    private static final ThreadLocal<englishStemmer> STEMMER =
            ThreadLocal.withInitial(englishStemmer::new);

    public Map<String, Integer> index(String content) {
        if (content == null || content.isEmpty()) {
            return new HashMap<>();
        }

        List<String> tokens = tokenize(content);
        Map<String, Integer> freq = new HashMap<>();

        for (String token : tokens) {
            freq.put(token, freq.getOrDefault(token, 0) + 1);
        }

        return freq;
    }

    private List<String> tokenize(String text) {
        return Arrays.stream(TOKEN.split(text.toLowerCase()))
                .filter(w -> !w.isEmpty())
                .filter(w -> w.length() > 2)
                .filter(w -> !w.matches("\\d+"))
                .filter(w -> !STOP_WORDS.contains(w))
                .map(this::stem)
                .toList();
    }

    private String stem(String word) {
        englishStemmer stemmer = STEMMER.get();
        stemmer.setCurrent(word);
        if (stemmer.stem()) {
            return stemmer.getCurrent();
        }
        return word;
    }
}
