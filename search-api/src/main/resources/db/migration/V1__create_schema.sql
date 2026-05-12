CREATE TABLE IF NOT EXISTS pages (
    id               INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url              TEXT UNIQUE NOT NULL,
    title            TEXT,
    description      TEXT,
    status           TEXT DEFAULT 'PENDING',
    content_hash     TEXT,
    crawl_depth      INTEGER DEFAULT 0,
    first_discovered TIMESTAMP DEFAULT NOW(),
    last_crawled     TIMESTAMP
);

CREATE TABLE IF NOT EXISTS page_content (
    page_id  INTEGER PRIMARY KEY REFERENCES pages(id),
    content  TEXT
);

CREATE TABLE IF NOT EXISTS links (
    from_id  INTEGER REFERENCES pages(id),
    to_id    INTEGER REFERENCES pages(id),
    PRIMARY KEY (from_id, to_id)
);

CREATE TABLE IF NOT EXISTS terms (
    id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    term          TEXT UNIQUE NOT NULL,
    doc_frequency INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS postings (
    term_id INTEGER REFERENCES terms(id),
    page_id INTEGER REFERENCES pages(id),
    freq    INTEGER NOT NULL,
    PRIMARY KEY (term_id, page_id)
);

CREATE TABLE IF NOT EXISTS indexing_queue (
    page_id      INTEGER PRIMARY KEY REFERENCES pages(id),
    status       TEXT DEFAULT 'pending',
    queued_at    TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP,
    error        TEXT,
    retry_count  INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_postings_term ON postings(term_id);
CREATE INDEX IF NOT EXISTS idx_queue_status ON indexing_queue(status)
    WHERE status = 'pending';