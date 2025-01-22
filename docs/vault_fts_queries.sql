# Example Queries for the FTS Table `articles_content_fts`

-- 1. Simple search: Find all chunks containing the word "machine learning".
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'machine learning';

-- 2. Search with filters: Find chunks containing "neural network" that belong to a specific source (`source`) and are of type `ABSTRACT`.
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'neural network'
AND source = 'arxiv'
AND chunk_type = 'ABSTRACT';

-- 3. Search with boolean operators: Find chunks containing "deep learning" and "computer vision", but not "tensorflow".
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'deep learning AND computer vision NOT tensorflow';

-- 4. Prefix and phrase search: Find chunks containing words starting with "data" or the exact phrase "natural language processing".
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'data* OR "natural language processing"';

-- 5. Search with OR operator and snippet: Find chunks containing "reinforcement learning" or "supervised learning" and return a highlighted snippet.
SELECT article_source, article_id, article_name, chunk_type, snippet(articles_content_fts, 2, '<b>', '</b>', '...', 10) AS highlighted_text
FROM articles_content_fts
WHERE chunk_text MATCH 'reinforcement learning OR supervised learning';

-- 6. Advanced search with multiple filters: Find chunks of type `TEXT` containing "generative adversarial networks" that belong to a specific source (`source`) and article (`article_id`).
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'generative adversarial networks'
AND source = 'medium'
AND article_id = 123
AND chunk_type = 'TEXT';

-- 7. Search with NEAR operator: Find chunks where "deep learning" and "neural networks" appear close to each other (within 5 words).
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'deep learning NEAR/5 neural networks';

-- 8. Search with relevance ranking: Find chunks containing "data analysis" and order them by relevance (using FTS5 `rank`).
SELECT article_source, article_id, article_name, chunk_type, chunk_text, rank
FROM articles_content_fts
WHERE chunk_text MATCH 'data analysis'
ORDER BY rank;

-- 9. Search with word exclusion: Find chunks containing "artificial intelligence" but not "ethics".
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'artificial intelligence NOT ethics';

-- 10. Search with Porter tokenization: Find chunks containing inflected forms of "learn" (e.g., "learning", "learned", etc.) thanks to Porter tokenization.
SELECT article_source, article_id, article_name, chunk_type, chunk_text
FROM articles_content_fts
WHERE chunk_text MATCH 'learn';