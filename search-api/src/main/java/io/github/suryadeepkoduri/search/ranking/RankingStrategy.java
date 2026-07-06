package io.github.suryadeepkoduri.search.ranking;

import io.github.suryadeepkoduri.search.ranking.model.ScoredDocument;
import java.util.List;

/**
 * RankingStrategy
 */
public interface RankingStrategy {
    List<ScoredDocument> rank(
        List<String> queryTerms,
        List<Integer> candidateDocIds
    );
}
