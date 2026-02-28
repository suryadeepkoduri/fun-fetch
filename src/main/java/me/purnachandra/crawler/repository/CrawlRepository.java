package me.purnachandra.crawler.repository;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.purnachandra.crawler.model.CrawlJob;
import me.purnachandra.crawler.model.ParsedPage;
import me.purnachandra.db.Database;

public class CrawlRepository {
    public List<CrawlJob> getNextPendingBatch(int batchSize) {
        String sql = """
                SELECT id,url,crawl_depth
                FROM pages
                WHERE status = 'pending'
                ORDER BY crawl_depth ASC
                LIMIT ?
                """;

        List<CrawlJob> jobs = new ArrayList<>();

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchSize);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int pageId = rs.getInt("id");
                String url = rs.getString("url");
                int depth = rs.getInt("crawl_depth");

                jobs.add(new CrawlJob(pageId, url, depth));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public Map<String, Integer> batchInsertUrls(List<String> urls, int depth) {
        String sql = """
                INSERT INTO pages(url,crawl_depth)
                SELECT unnest(?::text[]),?
                ON CONFLICT(url) DO NOTHING
                RETURNING id,url
                """;

        Map<String, Integer> urlToId = new HashMap<>();

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Array arr = conn.createArrayOf("text", urls.toArray());
            pstmt.setArray(1, arr);
            pstmt.setInt(2, depth);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                urlToId.put(rs.getString("url"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return urlToId;
    }

    public void saveCrawlContent(int pageId, ParsedPage parsedPage) {
        String updatePage = """
                UPDATE pages
                SET title=?, description=?,status='crawled',content_hash=?,last_crawled=NOW()
                where id=?
                """;

        String insertContent = """
                INSERT INTO page_content(page_id,content)
                VALUES(?,?)
                ON CONFLICT(page_id) DO UPDATE SET content=excluded.content
                """;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(updatePage)) {
                pstmt.setString(1, parsedPage.title());
                pstmt.setString(2, parsedPage.description());
                pstmt.setString(3, parsedPage.contentHash());
                pstmt.setInt(4, pageId);

                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertContent)) {
                pstmt.setInt(1, pageId);
                pstmt.setString(2, parsedPage.content());

                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getIdsByUrls(List<String> urls) {
        String sql = """
                SELECT id, url FROM pages
                WHERE url=ANY(?::text[])
                """;

        Map<String, Integer> urlToId = new HashMap<>();

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Array arr = conn.createArrayOf("text", urls.toArray());
            pstmt.setArray(1, arr);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                urlToId.put(rs.getString("url"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return urlToId;
    }

    public void batchInsertLinks(int fromId, List<Integer> toIds) {
        if (toIds.isEmpty())
            return;

        String sql = """
                    INSERT INTO links(from_id, to_id)
                    SELECT ?, unnest(?::int[])
                    ON CONFLICT(from_id, to_id) DO NOTHING
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            Array arr = conn.createArrayOf("integer", toIds.toArray());
            stmt.setInt(1, fromId);
            stmt.setArray(2, arr);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markFailed(int pageId) {
        String sql = """
                UPDATE pages
                SET status='failed',last_crawled=NOW()
                WHERE id=?
                """;

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pageId);
            pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
