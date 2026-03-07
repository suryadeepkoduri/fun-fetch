package me.purnachandra.search;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import me.purnachandra.db.Database;

@Service
public class SearchService {

    public List<SearchResult> search(String query, int limit) {

        String[] terms = query.toLowerCase().split("\\s+");

        int totalDocs = getTotalDocuments();
        List<SearchResult> results = new ArrayList<>();

        String sql = """
                    SELECT
                        p.id,
                        p.url,
                        p.title,
                        SUM(po.freq * LN(?::float / NULLIF(t.doc_frequency, 0))) AS score
                    FROM terms t
                    JOIN postings po ON po.term_id = t.id
                    JOIN pages p ON p.id = po.page_id
                    WHERE t.term = ANY(?)
                    GROUP BY p.id
                    ORDER BY score DESC
                    LIMIT ?
                """;
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, totalDocs);
            Array array = conn.createArrayOf("text", terms);
            pstmt.setArray(2, array);

            pstmt.setInt(3, limit);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SearchResult result = new SearchResult(
                        rs.getInt("id"),
                        rs.getString("url"),
                        rs.getString("title"),
                        rs.getDouble("score"));

                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private int getTotalDocuments() {
        String sql = "SELECT COUNT(*) FROM indexing_queue WHERE status='indexed'";
        try (Connection conn = Database.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();

            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}