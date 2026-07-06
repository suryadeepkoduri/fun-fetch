package io.github.suryadeepkoduri.search.ranking;

import io.github.suryadeepkoduri.search.ranking.model.ScoredDocument;
import java.util.List;

/**
 * BM25RankingStrategy
 */
public class BM25RankingStrategy implements RankingStrategy {

    @Override
    public List<ScoredDocument> rank(
        List<String> queryTerms,
        List<Integer> candidateDocIds
    ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rank'");
    }
}
