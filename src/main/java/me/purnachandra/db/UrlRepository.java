package me.purnachandra.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UrlRepository {
    public UrlRepository() {
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS urls(
                    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    url TEXT UNIQUE NOT NULL,
                    title TEXT,
                    description TEXT,
                    content TEXT,
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
        String sql = "INSERT INTO urls(url) VALUES(?) ON CONFLICT(url) DO NOTHING";

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

    public void addMetadata(String url, String title, String description) {
        String sql = "UPDATE urls SET title=?,description=? WHERE url = ?";

        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, url);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addData(String url, String title, String description, String content) {
        String sql = "UPDATE urls SET title=?,description=?, content=? WHERE url = ?";

        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, content);
            pstmt.setString(4, url);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIdByUrl(String url) {
        String sql = "SELECT id FROM urls WHERE url = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, url);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

}
