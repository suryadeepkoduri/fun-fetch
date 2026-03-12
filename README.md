# FunFetch

A search engine built from scratch in Java — BFS web crawler, inverted-index indexer, and TF-IDF search API.

Built as a learning project to understand how search engines actually work, from crawling to ranking.

---

## What it does

FunFetch crawls a set of seed URLs, stores discovered pages in PostgreSQL, indexes their content into an inverted index, and answers search queries ranked by TF-IDF.

```
Seed URLs → Crawler → pages + page_content + links tables
                    → indexing_queue
                              ↓
                          Indexer → terms + postings tables
                                           ↓
                              GET /search?q=java → ranked results
```

---

## Architecture

### Crawler (`me.purnachandra.crawler`)

- `CrawlerOrchestrator` — main BFS loop; pulls pending URLs in batches, processes each page
- `PageFetcher` — fetches pages via Jsoup with per-domain politeness delay and configurable timeout
- `PageParser` — extracts title, description, body content, outgoing links, and SHA-256 content hash
- `UrlProcessor` — normalises URLs (lowercased, fragment-stripped, trailing-slash normalised)
- `RulesEngine` — filters out non-crawlable URLs
- `CrawlRepository` — all DB writes; uses PostgreSQL `UNNEST` for batch inserts, `RETURNING id` to avoid extra lookups, transactional save for page + content + indexing_queue

### Indexer (`me.purnachandra.index`)

- `IndexOrchestrator` — reads from `indexing_queue`, processes pages in batches
- `Indexer` — tokenises content (lowercase, split on `\W+`), removes stop words and short tokens, returns term → frequency map
- `IndexRepository` — upserts into `terms` (updating `doc_frequency`) and `postings`

### Search (`me.purnachandra.search`)

- `SearchService` — executes TF-IDF query directly in SQL:

  ```
  score = SUM( freq * LN(totalDocs / doc_frequency) )
  ```

  Matches any query term, groups by page, orders by score descending
- `SearchController` — `GET /search?q=<query>&limit=<n>` returns JSON array of results

---

## Database Schema

```sql
-- Crawl
pages          (id, url, title, description, status, content_hash, crawl_depth, first_discovered, last_crawled)
page_content   (page_id → pages.id, content)
links          (from_id → pages.id, to_id → pages.id)

-- Index pipeline
indexing_queue (page_id → pages.id, status, queued_at, completed_at, error, retry_count)
terms          (id, term, doc_frequency)
postings       (term_id → terms.id, page_id → pages.id, freq)
```

---

## Stack

- **Java 21** · **Spring Boot** — REST API
- **PostgreSQL** (Neon serverless) — storage
- **HikariCP** — connection pooling
- **Jsoup** — HTML fetching and parsing

---

## Running it

### Prerequisites

- Java 21+
- Maven
- PostgreSQL database (see schema above)

### Setup

```bash
# Clone
git clone https://github.com/suryadeepkoduri/fun-fetch.git
cd fun-fetch

# Configure DB
cp .env.example .env
# Edit .env and set DB_URL, DB_USER, DB_PASSWORD
```

### Run the crawler

```bash
mvn exec:java -Dexec.mainClass="me.purnachandra.crawler.CrawlerMain"
```

### Run the indexer

```bash
mvn exec:java -Dexec.mainClass="me.purnachandra.index.IndexMain"
```

### Run the search API

```bash
mvn spring-boot:run
```

### Search

```
GET http://localhost:8080/search?q=java+spring&limit=10
```

Response:

```json
[
  {
    "id": 42,
    "url": "https://example.com/java-guide",
    "title": "Java Spring Boot Guide",
    "score": 4.87
  }
]
```

---

## Roadmap

### v1 (Current)

- BFS crawler with depth limit and politeness delays
- Decoupled indexer via `indexing_queue`
- TF-IDF scoring
- Spring Boot REST search API

### v2

- Multi-threaded crawler with `ExecutorService`
- BM25 ranking
- Field weighting (title 3× body)
- Stemming
- `robots.txt` support

### v3

- Simplified PageRank from `links` table
- Focused crawling with relevance scoring
- Recrawl scheduling based on `last_crawled` and content change
- Hybrid BM25 + PageRank scoring
- `/stats` endpoint with benchmark numbers
