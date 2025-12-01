package me.purnachandra.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class UrlRepository {
    public UrlRepository() {
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS urls(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    url TEXT UNIQUE,
                    status TEXT DEFAULT 'pending',
                    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUrl(String url) {
        String sql = "INSERT OR IGNORE INTO urls(url) VALUES(?)";

        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, url);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markVisited(String url) {
        String sql = "UPDATE urls SET status = 'visited' WHERE url = ?";

        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, url);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<String> getNextPendingUrl() {
        String sql = "SELECT url FROM urls WHERE status = 'pending' LIMIT 1";

        try (Statement stmt = Database.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
           if (rs.next()) {
                return Optional.of(rs.getString("url"));
           }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
