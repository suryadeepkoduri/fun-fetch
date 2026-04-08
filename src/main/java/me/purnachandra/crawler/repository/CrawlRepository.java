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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.purnachandra.crawler.CrawlStatus;
import me.purnachandra.crawler.model.CrawlJob;
import me.purnachandra.crawler.model.ParsedPage;
import me.purnachandra.db.Database;

public class CrawlRepository {
    private final Logger log = LoggerFactory.getLogger(CrawlRepository.class);

    public List<CrawlJob> getNextPendingBatch(int batchSize) {
        String sql = """
                SELECT id,url,crawl_depth
                FROM pages
                WHERE status = 'PENDING'
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
        } catch (SQLException e) {
            log.error("Failed to fetch pending batch of size {}", batchSize, e);
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
            log.error("Batch insert failed for {} URLs at depth {}", depth, urls, e);
        }

        return urlToId;
    }

    public void saveCrawlContent(int pageId, ParsedPage parsedPage) {
        String updatePage = """
                UPDATE pages
                SET title=?, description=?,status=?,content_hash=?,last_crawled=NOW()
                where id=?
                """;

        String insertContent = """
                INSERT INTO page_content(page_id,content)
                VALUES(?,?)
                ON CONFLICT(page_id) DO UPDATE SET content=excluded.content
                """;

        String insertIndexingQueue = """
                INSERT INTO indexing_queue(page_id,status)
                VALUES(?, 'pending')
                ON CONFLICT(page_id) DO NOTHING
                """;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement pstmt = conn.prepareStatement(updatePage)) {
                    pstmt.setString(1, parsedPage.title());
                    pstmt.setString(2, parsedPage.description());
                    pstmt.setString(3, CrawlStatus.SUCCESS.name());
                    pstmt.setString(4, parsedPage.contentHash());
                    pstmt.setInt(5, pageId);

                    pstmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertContent)) {
                    pstmt.setInt(1, pageId);
                    pstmt.setString(2, parsedPage.content());

                    pstmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertIndexingQueue)) {
                    pstmt.setInt(1, pageId);
                    pstmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.error("Failed to save crawl content for pageId {}, rolling back", pageId, e);
            }
        } catch (SQLException e) {
            log.error("Failed to get connection for pageId: {}", pageId, e);
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
            log.error("Failed to fetch IDs for URLs: {}", urls.size(), e);
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
            log.error("Failed to insert {} links from pageId {}", toIds.size(), fromId, e);
        }
    }

    public void markFailed(int pageId) {
        String sql = """
                UPDATE pages
                SET status=?,last_crawled=NOW()
                WHERE id=?
                """;

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, CrawlStatus.FAILED.name());
            pstmt.setInt(2, pageId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to mark pageId {} as failed", pageId, e);
        }
    }

    public void markNotAllowed(int pageId) {
        String sql = """
                UPDATE pages
                SET status=?,last_crawled=NOW()
                WHERE id=?
                """;

        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, CrawlStatus.NOT_ALLOWED.name());
            pstmt.setInt(2, pageId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to mark pageId {} as not allowed", pageId, e);
        }
    }
}
