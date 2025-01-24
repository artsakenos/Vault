Wikipedia is an online encyclopedia project that is multilingual, free, and collaboratively written by volunteers.

# API

[Wikipedia API SandBox](https://en.wikipedia.org/wiki/Special:ApiSandbox)

# Dumps

Wikimedia provides [dumps](https://dumps.wikimedia.org/backup-index.html).
They contain backups of various projects: wikipedia, wikiquote, wikivoyage, ...
Simply choose the dumps related to your selected language. For example, for Chinese language (zh):
* zhwiki - Wikipedia
* zhwiktionary - Dictionary
* zhwikisource - WikiSource
* zhwikibooks - Books
* ...

Let's open the latest [zhwiki](https://dumps.wikimedia.org/zhwiki/20250101/), ensuring that the dump is *complete*.

## Structured Dumps

For the vault, dumps in [another format](https://dumps.wikimedia.org/other/) are used,
specifically, a [well-structured JSON format](https://dumps.wikimedia.org/other/enterprise_html/).
From here, look for the [directory](https://dumps.wikimedia.org/other/enterprise_html/runs/20250101/) inside *runs/*.
Note that, for example, in *zhwiki-NS0-20250101-ENTERPRISE-HTML.json.tar.gz*:
* zh: ISO 639-1 code for Chinese language
* wiki indicates it's a Wikipedia project dump
* NS0: Identifies the namespace within the project
    * **NS0** (the one we need) refers to main articles or standard Wikipedia content pages
    * NS1: Article discussions
    * NS4: Wikipedia (pages related to the project itself)
    * NS8: MediaWiki (system pages for user interface)
    * NS10: Reusable templates
    * NS14: Categories for organizing pages
    * NS6: Multimedia files with details and licenses
* 20241201: Dump date
* ENTERPRISE-HTML: Specifies that the file contains pre-processed HTML data from the Enterprise service
* .json format, and .tar.gz compression

## Download Procedure
1. Visit one of the runs pages, selected from: https://dumps.wikimedia.org/other/enterprise_html/runs/
2. Select simplewiki-NS0-... (assuming you've chosen simplewiki)
3. Create and enter the folder db/dumps/simplewiki
4. wget https://dumps.wikimedia.org/other/enterprise_html/runs/20250101/simplewiki-NS0-20250101-ENTERPRISE-HTML.json.tar.gz
5. tar -xzvf ...
6. Visit the vault /import page

Some example dumps:
* [Sardinia](https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/scwiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz) (30 MB)
* [Italy](https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/itwiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz) (24 GB)
* [SimpleWiki](https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/simplewiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz) (2 GB)

# Entities

Entity Examples
* [Q3282218 - Malloreddus](https://www.wikidata.org/wiki/Q3282218)
* [Q3282218 - Malloreddus, Json](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q3282218&format=json)
* [Q186538 - Arzachena, Json](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q186538&format=json)

# Statements / Claims

Within entities, there are all structured properties (statements).
Here's the [List of Properties](https://www.wikidata.org/wiki/Wikidata:List_of_properties)

Examples with [Garibaldi - Q359](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q539&format=json)
* P569 Is the date of birth datatype": "time" → "datavalue": { "value": { "time": "+1807-07-04T00:00:00Z",
* P119 Is the place of burial, datatype: "wikibase-item" → points to Q845310 (Caprera) → "datavalue": { "value": { "id": "Q845310" } }

Examples with [Arzachena](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q186538&format=json)
* P625 - Coordinates (then there are also southernmost, etc.)

Instead of making a query per page, or a bulk query, you can download the:
List of all [Semantic Units - 86GB](https://dumps.wikimedia.org/wikidatawiki/entities/latest-all.json.bz2)

Here's the [index](https://dumps.wikimedia.org/wikidatawiki/entities/):

    latest-all.json.bz2      - 86.4 GB
    latest-all.json.gz       - 131.2 GB
    latest-all.nt.bz2        - 166.1 GB
    latest-all.nt.gz         - 215.2 GB
    latest-all.ttl.bz2       - 106.2 GB
    latest-all.ttl.gz        - 129.9 GB
    latest-lexemes.json.bz2  - 0.33 GB
    latest-lexemes.json.gz   - 0.45 GB
    latest-lexemes.nt.bz2    - 0.84 GB
    latest-lexemes.nt.gz     - 1.09 GB
    latest-lexemes.ttl.bz2   - 0.48 GB
    latest-lexemes.ttl.gz    - 0.60 GB
    latest-truthy.nt.bz2     - 37.2 GB
    latest-truthy.nt.gz      - 61.4 GB

* .json: data in JSON format, easier to process programmatically
* .nt: N-Triples format, great for RDF/linked data
* .ttl: Turtle format, more readable than N-Triples, still for RDF
* latest-all: contains all Wikidata data
* latest-lexemes: contains only lexicographical data (dictionary)
* latest-truthy: contains only current "truthful" statements, without history and metadata
* .bz2 vs .gz: these are two different compression methods, bz2 generally compresses better but is slower

## Attempts to Reduce Dump Size

The ideas are:
* Use only Italian Wikipedia and filter manually (for example all highways, championships, ...)
* Use only SimpleWiki (which is already a selection in itself)
* Retrieve only desired articles, for example those in Italian tagged as vital

  SELECT ?article ?articleLabel WHERE {
  ?article wdt:P31 wd:Q13442814. # Filter for Wikipedia articles
  ?article wdt:P7937 wd:Q21988530. # Tag Vital Articles
  SERVICE wikibase:label { bd:serviceParam wikibase:language "it". }
  }

[Corresponding similar query.](https://query.wikidata.org/#SELECT%20%3Farticle%20%3FarticleLabel%20WHERE%20%7B%0A%20%20%3Farticle%20wdt%3AP31%20wd%3AQ13442814.%20%23%20Filtra%20per%20articoli%20di%20Wikipedia%0A%20%20%3Farticle%20wdt%3AP7937%20wd%3AQ21988530.%20%23%20Tag%20Vital%20Articles%0A%20%20SERVICE%20wikibase%3Alabel%20%7B%20bd%3AserviceParam%20wikibase%3Alanguage%20%22en%22.%20%7D%0A%7D)
