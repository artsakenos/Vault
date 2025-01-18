SELECT
    ac.source,
    ac.article_id,
    ac.chunk_type,
    ac.chunk_id,
    ac.chunk_text
FROM
    article_chunks ac
LEFT JOIN
    article_embeddings ae
ON
    ac.source = ae.source
    AND ac.article_id = ae.article_id
    AND ac.chunk_type = ae.chunk_type
    AND ac.chunk_id = ae.chunk_id
    AND ae.embedding_model = ?
WHERE
    (ac.chunk_type = 'ABSTRACT' OR ac.chunk_type = 'TEXT')
    AND ae.embedding_model IS NULL
LIMIT 10;