-- Create 'articles' table to store articles
CREATE TABLE articles (
    source TEXT NOT NULL,  -- Source of the article (e.g., wiki, pdf, txt, img, audio, web)
    id TEXT NOT NULL,      -- Article ID, e.g., the Wikipedia ID, filename, etc.
    name TEXT NOT NULL,    -- Article name, filename, title, etc.
    description TEXT,      -- Content summary (around 500 characters?, e.g., abstract) preferably in simple English
    PRIMARY KEY (source, id)  -- Primary key composed of source and id
);

-- Insert an example article
INSERT INTO articles (source, id, name, description)
VALUES ('test', '12345', 'Example Article', 'This is an example article with an abstract.');

-- Create 'article_categories' table to store article categories
CREATE TABLE article_categories (
    source TEXT NOT NULL,                   -- Article source
    article_id TEXT NOT NULL,               -- Article ID
    category TEXT NOT NULL,                 -- Article category, preferably in lowercase
    PRIMARY KEY (source, article_id, category),  -- Primary key composed of source, article_id and category
    FOREIGN KEY (source, article_id) REFERENCES articles(source, id)
        ON UPDATE CASCADE ON DELETE CASCADE  -- Foreign key referencing articles(source, id)
);

-- Insert an example category
INSERT INTO article_categories (source, article_id, category)
VALUES ('test', '12345', 'history');

-- Create 'article_tags' table to store article tags
CREATE TABLE article_tags (
    source TEXT NOT NULL,      -- Article source
    article_id TEXT NOT NULL,  -- Article ID
    tag TEXT NOT NULL,         -- Article tag, preferably in lowercase
    PRIMARY KEY (source, article_id, tag),  -- Primary key composed of source, article_id and tag
    FOREIGN KEY (source, article_id) REFERENCES articles(source, id)
        ON UPDATE CASCADE ON DELETE CASCADE  -- Foreign key referencing articles(source, id)
);

-- Insert an example tag
INSERT INTO article_tags (source, article_id, tag)
VALUES ('test', '12345', 'middle_ages');

-- Create 'article_chunks' table to store article chunks
-- Chunking types:
-- - TEXT: pure text
-- - ITERATIVE: text chunked iteratively based on fixed size and overlap
-- - STRUCTURED: text chunked based on structure (e.g., paragraphs, sections, etc.)
-- - RESUME: text chunked based on a summary, e.g., performed by an LLM
-- - HTML: HTML Document
-- - MARKDOWN: Markdown Document
CREATE TABLE article_chunks (
    source TEXT NOT NULL,      -- Article source
    article_id TEXT NOT NULL,  -- Article ID
    chunk_type TEXT NOT NULL,  -- Chunk type (e.g., TEXT, ITERATIVE, STRUCTURED, RESUME, HTML, MARKDOWN, etc.)
    chunk_section TEXT,        -- Chunk Section, if available (e.g., abstract, biography, etc.)
    chunk_id INTEGER NOT NULL, -- Chunk ID
    chunk_count INTEGER,       -- Total chunk count
    chunk_text TEXT,           -- Chunk text
    PRIMARY KEY (source, article_id, chunk_type, chunk_id),  -- Primary key composed of source, article_id, chunk_type and chunk_id
    FOREIGN KEY (source, article_id) REFERENCES articles(source, id)
        ON UPDATE CASCADE ON DELETE CASCADE  -- Foreign key referencing articles(source, id)
);

-- Insert example chunks showing different sections of an article
INSERT INTO article_chunks (source, article_id, chunk_type, chunk_section, chunk_id, chunk_count, chunk_text)
VALUES
    ('test', '67890', 'ITERATIVE', NULL, 1, 2, 'This is the first half of the text content.'),
    ('test', '12345', 'STRUCTURED', 'abstract', 1, 3, 'This is the article abstract.'),
    ('test', '12345', 'STRUCTURED', 'references', 3, 3, 'List of references and sources.');

-- Create 'article_embeddings' table to store chunk embeddings
-- embedding_model: usually the embedding id on hugging face, e.g.
--  - sentence-transformers/all-MiniLM-L6-v2
--  - nickprock/sentence-bert-base-italian-uncased
CREATE TABLE article_embeddings (
    source TEXT NOT NULL,          -- Article source
    article_id TEXT NOT NULL,      -- Article ID
    chunk_type TEXT NOT NULL,      -- Reference to chunk type
    chunk_id INTEGER NOT NULL,     -- Reference to chunk ID
    embedding_model TEXT NOT NULL,  -- Embedding Model
    embedding_vector BLOB,         -- Embedding vector
    PRIMARY KEY (source, article_id, chunk_type, chunk_id, embedding_model),  -- Primary key including all chunk reference fields
    FOREIGN KEY (source, article_id, chunk_type, chunk_id)
        REFERENCES article_chunks(source, article_id, chunk_type, chunk_id)
        ON UPDATE CASCADE ON DELETE CASCADE  -- Foreign key referencing the specific chunk
);

-- Insert example embeddings for different chunks
INSERT INTO article_embeddings (source, article_id, chunk_type, chunk_id, embedding_model, embedding_vector)
VALUES
    -- Embedding for a structured chunk (abstract section)
    ('test', '12345', 'STRUCTURED', 1, 'sentence-transformers/all-MiniLM-L6-v2', x'0102030405060708090A'),
    -- Embedding for another structured chunk (biography section)
    ('test', '12345', 'STRUCTURED', 2, 'sentence-transformers/all-MiniLM-L6-v2', x'0A090807060504030201'),
    -- Embedding for a plain text chunk
    ('test', '67890', 'ITERATIVE', 1, 'nickprock/sentence-bert-base-italian-uncased', x'0123456789ABCDEF0123');

-- Create 'article_meta' table to store article metadata
CREATE TABLE article_meta (
    source TEXT NOT NULL,      -- Article source
    article_id TEXT NOT NULL,  -- Article ID
    meta_type TEXT NOT NULL,   -- Metadata type (e.g., Wikipedia EntityID, Location, Date_start, Date_end, Author, etc.)
    meta_value TEXT,           -- Metadata value
    PRIMARY KEY (source, article_id, meta_type),  -- Primary key composed of source, article_id and meta_type
    FOREIGN KEY (source, article_id) REFERENCES articles(source, id)
        ON UPDATE CASCADE ON DELETE CASCADE  -- Foreign key referencing articles(source, id)
);

-- Insert an example metadata
INSERT INTO article_meta (source, article_id, meta_type, meta_value)
VALUES ('test', '12345', 'Author', 'John Smith');


-- Create FTS virtual table for article chunks
-- This indexes both the basic article info and the chunks content
CREATE VIRTUAL TABLE IF NOT EXISTS articles_content_fts USING fts5(
    source,           -- Include source for better filtering
    article_id,       -- Added for joining
    article_name,     -- Article name for basic searches
    chunk_type,       -- Include chunk type for filtering
    chunk_text,       -- The actual content to search
    prefix='2,3',
    tokenize='porter unicode61'
);

-- Trigger for article insertion/updates
CREATE TRIGGER IF NOT EXISTS articles_ai AFTER INSERT ON articles BEGIN
    INSERT INTO articles_content_fts (
        source,
        article_id,
        article_name,
        chunk_type,
        chunk_text
    )
    VALUES (
        new.source,
        new.id,
        new.name,
        'ARTICLE',
        new.description
    );
END;

-- Trigger for chunk insertion
CREATE TRIGGER IF NOT EXISTS article_chunks_ai AFTER INSERT ON article_chunks BEGIN
    INSERT INTO articles_content_fts (
        source,
        article_id,
        article_name,
        chunk_type,
        chunk_text
    )
    SELECT
        new.source,
        new.article_id,
        a.name,
        new.chunk_type,
        new.chunk_text
    FROM articles a
    WHERE a.source = new.source
    AND a.id = new.article_id
    AND new.chunk_type IN ('TEXT', 'ABSTRACT');
END;

-- Delete trigger for articles
CREATE TRIGGER IF NOT EXISTS articles_ad AFTER DELETE ON articles BEGIN
    DELETE FROM articles_content_fts
    WHERE source = old.source AND article_id = old.id;
END;

-- Delete trigger for chunks
CREATE TRIGGER IF NOT EXISTS article_chunks_ad AFTER DELETE ON article_chunks BEGIN
    DELETE FROM articles_content_fts
    WHERE source = old.source
    AND article_id = old.article_id
    AND chunk_type = old.chunk_type;
END;
