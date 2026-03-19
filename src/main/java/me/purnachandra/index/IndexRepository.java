package me.purnachandra.index;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.purnachandra.db.Database;

public class IndexRepository {
    private final Logger log = LoggerFactory.getLogger(IndexRepository.class);

    public List<Integer> getNextPendingBatch(int batchSize) {
        String sql = "SELECT page_id FROM indexing_queue WHERE status='pending' LIMIT ?";

        List<Integer> batch = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchSize);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                batch.add(rs.getInt("page_id"));
            }
        } catch (Exception e) {
            log.error("Error fetching next pending batch", e);
        }
        return batch;
    }

    public void addIndex(int pageId, Map<String, Integer> freqs) {
        Map<String, Integer> allTerms = addTerms(new ArrayList<>(freqs.keySet()));

        String sql = """
                INSERT INTO postings(term_id,page_id,freq)
                SELECT unnest(?::int[]),?,unnest(?::int[])
                """;

        String markIndexedSql = "UPDATE indexing_queue SET status='indexed' WHERE page_id=?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                PreparedStatement markIndexedPstmt = conn.prepareStatement(markIndexedSql)) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(freqs.entrySet());
            Integer[] termIds = entries.stream().map(e -> allTerms.get(e.getKey())).toArray(Integer[]::new);
            Integer[] termFreqs = entries.stream().map(Map.Entry::getValue).toArray(Integer[]::new);
            java.sql.Array sqlTermIds = conn.createArrayOf("int", termIds);
            java.sql.Array sqlTermFreqs = conn.createArrayOf("int", termFreqs);
            pstmt.setArray(1, sqlTermIds);
            pstmt.setInt(2, pageId);
            pstmt.setArray(3, sqlTermFreqs);
            pstmt.executeUpdate();
            markIndexedPstmt.setInt(1, pageId);
            markIndexedPstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error adding index", e);
        }
    }

    public String fetchPageContent(int pageId) {
        String sql = "SELECT content FROM page_content WHERE page_id=?";
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pageId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("content");
            }
        } catch (Exception e) {
            log.error("Error fetching page content for page ID {}", pageId, e);
        }
        return "";
    }

    public Map<String, Integer> addTerms(List<String> terms) {
        String sql = """
                INSERT INTO terms(term)
                SELECT unnest(?::text[])
                ON CONFLICT(term) DO UPDATE SET doc_frequency = terms.doc_frequency + 1
                RETURNING id,term
                """;
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            java.sql.Array sqlArray = conn.createArrayOf("text", terms.toArray());
            pstmt.setArray(1, sqlArray);
            var rs = pstmt.executeQuery();
            Map<String, Integer> termToId = new HashMap<>();
            while (rs.next()) {
                termToId.put(rs.getString("term"), rs.getInt("id"));
            }
            return termToId;
        } catch (SQLException e) {
            log.error("Error adding terms", e);
        }
        return new HashMap<>();
    }

    public Map<String, Integer> getTermIds(List<String> terms) {
        String sql = """
                SELECT id,term
                FROM terms
                WHERE term = ANY(?::text[])
                """;
        Map<String, Integer> termToId = new HashMap<>();
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            java.sql.Array sqlArray = conn.createArrayOf("text", terms.toArray());
            pstmt.setArray(1, sqlArray);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                termToId.put(rs.getString("term"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            log.error("Error fetching term IDs", e);
        }
        return termToId;
    }
}
