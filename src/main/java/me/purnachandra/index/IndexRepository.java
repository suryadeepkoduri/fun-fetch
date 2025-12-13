package me.purnachandra.index;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

import me.purnachandra.db.Database;

public class IndexRepository {

    public IndexRepository() {
        createTable();
    }

    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS postings(
                    term TEXT NOT NULL,
                    doc_id INTEGER NOT NULL,
                    freq INTEGER NOT NULL,
                    PRIMARY KEY(term,doc_id)
                );

                CREATE INDEX IF NOT EXISTS idx_term ON postings(term);
                """;

        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addIndex(int docId, Map<String, Integer> freqs) {
        String sql = "INSERT INTO postings(term,doc_id,freq) VALUES(?,?,?) ON CONFLICT(term,doc_id) DO UPDATE SET freq=excluded.freq";

        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
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
