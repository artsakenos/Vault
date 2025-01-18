CREATE TABLE IF NOT EXISTS wiki_articles (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    abstract_text TEXT,
    body_text TEXT,
    body_wiki TEXT,
    body_html TEXT,
    image_url TEXT,
    language_id TEXT,
    wiki_id TEXT NOT NULL,
    entity_id TEXT
);

CREATE TABLE IF NOT EXISTS wiki_article_categories (
    article_id INTEGER NOT NULL,
    category TEXT NOT NULL,
    PRIMARY KEY (article_id, category),
    FOREIGN KEY (article_id) REFERENCES wiki_articles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wiki_article_tags (
    article_id INTEGER NOT NULL,
    tag TEXT NOT NULL,
    PRIMARY KEY (article_id, tag),
    FOREIGN KEY (article_id) REFERENCES wiki_articles(id) ON DELETE CASCADE
);

-- Tables for FTS --

CREATE VIRTUAL TABLE IF NOT EXISTS wiki_articles_fts USING fts5(
    name, abstract_text, body_text
);

CREATE TRIGGER IF NOT EXISTS wiki_articles_ai AFTER INSERT ON wiki_articles BEGIN
    INSERT INTO wiki_articles_fts (rowid, name, abstract_text, body_text) VALUES (new.id, new.name, new.abstract_text, new.body_text);
END;

CREATE TRIGGER IF NOT EXISTS wiki_articles_ad AFTER DELETE ON wiki_articles BEGIN
    INSERT INTO wiki_articles_fts (wiki_articles_fts, rowid, name, abstract_text, body_text) VALUES ('delete', old.id, old.name, old.abstract_text, old.body_text);
END;

CREATE TRIGGER IF NOT EXISTS wiki_articles_au AFTER UPDATE ON wiki_articles BEGIN
    INSERT INTO wiki_articles_fts (wiki_articles_fts, rowid, name, abstract_text, body_text) VALUES ('delete', old.id, old.name, old.abstract_text, old.body_text);
    INSERT INTO wiki_articles_fts (rowid, name, abstract_text, body_text) VALUES (new.id, new.name, new.abstract_text, new.body_text);
END;