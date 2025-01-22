
# Wikipedia Categories Analysis

A key operation to build an optimal and efficient vault KB is to make a selection of the relevant articles to be indexed. 
This is a crucial step to avoid indexing irrelevant articles and to focus on the most important ones.
This document provides an analysis of categories extracted from the **Simple English Wikipedia Dump (December 2024)**.

## Overview

### Query: Most Frequent Categories

The following SQL query retrieves the most frequent categories in the dataset:

```sql
SELECT category, COUNT(*) AS num
FROM wiki_article_categories
GROUP BY category
ORDER BY num DESC;
```

#### Result

| Category                                    | Count  |
|---------------------------------------------|--------|
| Pages using gadget WikiMiniAtlas            | 35,637 |
| Living people                               | 33,695 |
| Coordinates on Wikidata                     | 31,486 |
| Webarchive template wayback links           | 28,239 |
| People stubs                                | 21,941 |
| Articles with hCards                        | 20,676 |
| Articles with VIAF identifiers              | 18,938 |
| United States geography stubs               | 18,326 |
| Articles with LCCN identifiers              | 15,742 |
| Commons category link from Wikidata         | 15,040 |
| France geography stubs                      | 14,720 |
| Articles with GND identifiers               | 14,047 |
| Europe stubs                                | 14,044 |
| American people stubs                       | 13,284 |
| Sportspeople stubs                          | 13,229 |
| Articles with J9U identifiers               | 13,208 |
| Commons category link is on Wikidata        | 12,379 |
| Articles with WorldCat Entities identifiers | 10,525 |

---

### Incremental Indexing: Specific Class of Interest

If I'd want to filter categories incrementally, for example, focusing on articles related to "survival",
this in order to retrieve the data I'm most interested in:

```sql
SELECT category, COUNT(*) AS num
FROM wiki_article_categories
WHERE category LIKE '%surv%'
GROUP BY category
ORDER BY num DESC;
```

#### Result

| Category                                                            | Count |
|---------------------------------------------------------------------|-------|
| Holocaust survivors                                                 | 122   |
| Survival video games                                                | 38    |
| Survivor Series                                                     | 29    |
| Movies about security and surveillance                              | 15    |
| Survival movies                                                     | 14    |
| Survivor seasons                                                    | 12    |
| Assassination survivors                                             | 11    |
| Survival skills                                                     | 7     |
| Smallpox survivors                                                  | 7     |
| RMS Titanic survivors                                               | 6     |
| Survival horror video games                                         | 5     |
| Wikipedia articles incorporating text from the US Geological Survey | 1     |
| Survivor (U.S. TV series)                                           | 1     |
| Survivor (TV series)                                                | 1     |


There are so many categories I'm not interested about.

---

### Example: Exploring a Specific Category

Let's analyze a particular category, such as "Survival skills,":

```sql
SELECT * 
FROM wiki_articles wa, wiki_article_categories wac
WHERE wac.article_id = wa.id
AND category LIKE '%Survival skills%';
```

#### Result

| Article ID | Title               | Wikidata ID |
|------------|---------------------|-------------|
| 10,000     | Fishing             | Q14373      |
| 16,573     | Troll (Internet)    | Q11281      |
| 8,580      | Swim                | Q115264606  |
| 7,824      | Giovanni da Capestrano | Q310359  |
| 505,214    | Ice fishing         | Q1313678    |
| 254,894    | Winter swimming     | Q731736     |
| 1,051,170  | Survival kit        | Q2594376    |

Again, so many articles I'm not intersted about, and so few that are actually relevant.

---

### Observations

- Many categories, such as "Holocaust survivors" and "Survival video games," are not directly relevant to indexing survival-related articles.
- Within specific categories like "Survival skills," the number of articles of genuine interest is limited.
- Filtering and refining the dataset is necessary to target meaningful content.

---

## Numerosità e dimensione dei MD

Senza Chunking:
45943	84	2509	1248

Con Chunking:
45943	84	2509	1248

Differenza tra testo, html e markdown

