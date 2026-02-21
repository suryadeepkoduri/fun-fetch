package me.purnachandra.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.purnachandra.index.Indexer;

public class SearchService {
    private final Connection connection;

    public SearchService(Connection connection) {
        this.connection = connection;
    }

    public List<SearchResult> search(String query, int limit) throws SQLException {
        String[] terms = Indexer.tokenize(query);
        Map<Integer, Double> scores = new HashMap<>();

        String sql = """
                SELECT doc_id, freq
                FROM postings
                WHERE term=?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String term : terms) {
                stmt.setString(1, term);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int docId = rs.getInt("doc_id");
                    int freq = rs.getInt("freq");

                    scores.merge(docId, (double) freq, Double::sum);
                }
            }
        }

        List<Map.Entry<Integer,Double>> ranked = new ArrayList<>(scores.entrySet());
        ranked.sort((a,b) -> Double.compare(b.getValue(), a.getValue()));

        List<SearchResult> results = new ArrayList<>();

        String docSql = """
                SELECT id, url, title
                FROM urls
                WHERE id=?
                """;
        
        try (PreparedStatement stmt = connection.prepareStatement(docSql)) {
            int count = 0;
            for (Map.Entry<Integer, Double> entry : ranked)  {
                if(count >= limit)
                    break;
                
                stmt.setInt(1, entry.getKey());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    results.add(new SearchResult(
                            rs.getInt("id"),
                            rs.getString("url"),
                            rs.getString("title"),
                            entry.getValue()
                    ));
                }
                count++;
            }
        }
        return results;
    }
}
