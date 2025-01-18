SELECT
	a.source, a.id, a.name,
	afts.chunk_type, afts.chunk_text,
	rank -- synonim of bm25(articles_content_fts) as score
FROM
	articles a INNER JOIN articles_content_fts afts
	ON a.id = afts.article_id
WHERE
	articles_content_fts MATCH ?
ORDER BY rank ASC
LIMIT 10
