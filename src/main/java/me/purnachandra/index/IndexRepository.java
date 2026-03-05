package me.purnachandra.index;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import me.purnachandra.db.Database;

public class IndexRepository {

    public void addIndex(int docId, Map<String, Integer> freqs) {
        String sql = "INSERT INTO postings(term,doc_id,freq) VALUES(?,?,?) ON CONFLICT(term,doc_id) DO UPDATE SET freq=excluded.freq";

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (var e : freqs.entrySet()) {
                pstmt.setString(1, e.getKey());
                pstmt.setInt(2, docId);
                pstmt.setInt(3, e.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
