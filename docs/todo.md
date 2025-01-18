
## Soluzione con Embedding

Già implementata negli UltraServices con esempi e test su differenti embedders.


## TODO

- [ ] SqliteService chiama un init come Database initializr, duplicato. Se lo togli rompe l'slite connector però.
- [ ] Risolvere il problema che nella query mette i ' e rompe la ricerca


[BAAI/bge-m3](https://huggingface.co/BAAI/bge-m3)



-- Search across all content
SELECT * FROM articles_content_fts
WHERE articles_content_fts MATCH 'your_search_term';

-- Search in specific chunk types
SELECT * FROM articles_content_fts
WHERE articles_content_fts MATCH 'chunk_type:STRUCTURED your_search_term';

-- Combined metadata and content search
SELECT * FROM articles_content_fts
WHERE articles_content_fts MATCH 'article_name:history chunk_text:revolution';


