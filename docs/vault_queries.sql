
-- Articles with Chunks
SELECT *
FROM articles a, article_chunks ac
WHERE   a."source" = ac.article_source
    AND a.id = ac.article_id
    AND a.id = '1899'


-- Numerosità e Dimensioni Tabelle
SELECT
    MAX(ctl) as max, MIN(ctl) as min, CAST(AVG(ctl) as INTEGER) as avg, Count(*) as tot
FROM (
    SELECT article_id, LENGTH(chunk_text) as ctl
    FROM article_chunks ac
    WHERE ac.chunk_type = 'MARKDOWN'
    AND   ac.chunk_count = 0  ) -- 0 = senza chunking, <>0 con chunking


-- Lunghezza media in base al tipo di Chunk
SELECT article_id, chunk_type, AVG(LENGTH(chunk_text)) as ctl
FROM article_chunks ac
WHERE ac.chunk_count = 0
GROUP BY chunk_type



