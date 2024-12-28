

# Architettura

Il progetto memorizza e indicizza una proiezione di informazioni presenti sui dump di Wikipedia.
Integra un DB sqlite con funzionalità di ricerca (FTS) e vettoriali.
Un DB H2 viene utilizzato per logging e per memorizzare le configurazioni.

Il progetto è un clone in Java creato a partire da [WikiLite](https://github.com/eja/wikilite) 
per fare qualche esperimento sui dati.

## Setup
* Nota: i seguenti step sono da automatizzare.
* Scaricare un dump ad esempio 
  * cd db/dump_simplewiki
  * wget https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/simplewiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz
  * tar -xzvf ...
* Fare il parsing della directory http://localhost:8181/vault/parse_dir?dirPath=./db/dump_simplewiki
* O di un file http://localhost:8181/vault/parse_file?filePath=./db/dump_simplewiki/simplewiki_namespace_0_5.ndjson


# API

[Wikipedia API SandBox](https://en.wikipedia.org/wiki/Special:ApiSandbox)


# Dumps

Vengono usati dei dump in formato json ben strutturato.

https://dumps.wikimedia.org/other/enterprise_html/

https://dumps.wikimedia.org/backup-index.html

Alcuni dump
* [Sardinia](https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/scwiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz) (30 MB)
* [Italy](https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/itwiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz) (24 GB)
* [SimpleWIki](https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/simplewiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz) (2 GB)

# Entities

Esempi di Entities
* [Q3282218 - Malloreddus](https://www.wikidata.org/wiki/Q3282218)
* [Q3282218 - Malloreddus, Json](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q3282218&format=json)
* [Q186538 - Arzachena, Json](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q186538&format=json)

# Statements / Claims

All'interno delle entities ci sono tutte le proprties strutturate (statements).
Ecco la [Lista di Properties](https://www.wikidata.org/wiki/Wikidata:List_of_properties)

Esempi con [Garibaldi - Q359](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q539&format=json)
* P569 E’ la data di nascita datatype": "time" → "datavalue": { "value": { "time": "+1807-07-04T00:00:00Z",
* P119 E’ il luogo di sepoltura, datatype": "wikibase-item" – punta a → Q845310 (Caprera) → "datavalue": { "value": { "id": "Q845310"

Esempi con [Arzachena](https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q186538&format=json)
* P625 - Coordinate (poi ci sono anche le souternmost, etc.)


Invece che fare una query per pagina, o un bulk, si può scaricare la:
Lista di tutte le [Unità Semantiche - 86GB](https://dumps.wikimedia.org/wikidatawiki/entities/latest-all.json.bz2)

Ecco l'[indice](https://dumps.wikimedia.org/wikidatawiki/entities/).

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

* .json: dati in formato JSON, più facile da processare programmaticamente
* .nt: formato N-Triples, ottimo per RDF/linked data
* .ttl: formato Turtle, più leggibile di N-Triples, sempre per RDF
* latest-all: contiene tutti i dati di Wikidata
* latest-lexemes: contiene solo i dati lessicografici (dizionario)
* latest-truthy: contiene solo le dichiarazioni "veritiere" attuali, senza storico e metadati
* .bz2 vs .gz: sono due diversi metodi di compressione, bz2 generalmente comprime meglio ma è più lento


## Tentativi di ridurre le dimensioni del dump

Le idee sono:
* Utilizzare solo Wikipedia in Italiano e filtrare a mano (ad esempio tutte le autostrade, gli scudetti, ...)
* Utilizzare solo SimpleWIki (che di per se è già una selezione)
* Recuperare solo gli articoli desiderati, ad esempio quelli in italiano taggati come vital


    SELECT ?article ?articleLabel WHERE {
    ?article wdt:P31 wd:Q13442814. # Filtra per articoli di Wikipedia
    ?article wdt:P7937 wd:Q21988530. # Tag Vital Articles
    SERVICE wikibase:label { bd:serviceParam wikibase:language "it". }
    }


[Query simile corrispondente.](https://query.wikidata.org/#SELECT%20%3Farticle%20%3FarticleLabel%20WHERE%20%7B%0A%20%20%3Farticle%20wdt%3AP31%20wd%3AQ13442814.%20%23%20Filtra%20per%20articoli%20di%20Wikipedia%0A%20%20%3Farticle%20wdt%3AP7937%20wd%3AQ21988530.%20%23%20Tag%20Vital%20Articles%0A%20%20SERVICE%20wikibase%3Alabel%20%7B%20bd%3AserviceParam%20wikibase%3Alanguage%20%22en%22.%20%7D%0A%7D)


# Development

[Initizlizr](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.4.1&packaging=jar&jvmVersion=17&groupId=tk.artsakenos&artifactId=vault&name=Vault&description=A%20Vault%20with%20Knowledge&packageName=tk.artsakenos.vault&dependencies=web,lombok,devtools,thymeleaf,data-jpa,h2,spring-ai-ollama)

-------------------------------------


# OpenStreetMap

For downloading a complete OpenStreetMap (OSM) planet dump, here are your main options:
From planet.openstreetmap.org:
https://planet.openstreetmap.org/planet/planet-latest.osm.pbf


# Test e WIP

    SELECT
        w.id,
        w.name,
        w.abstract_text,
        bm25(wiki_articles_fts) as score, -- sinonimo di rank
    FROM wiki_articles w
        INNER JOIN wiki_articles_fts ON w.id = wiki_articles_fts.rowid
        WHERE wiki_articles_fts MATCH 'garibaldi OR volcanic'
    ORDER BY rank ASC
    LIMIT 10

## Esempi di Pattern Matching

    -- Ricerca con prefisso per varianti di una parola
    WHERE wiki_articles_fts MATCH 'ital*'  -- matches: italy, italian, italians, italia
    
    -- Ricerca di una frase esatta (le parole nell'ordine specificato)
    WHERE wiki_articles_fts MATCH '"ancient roman empire"'
    
    -- Combinazione di exact phrase e wildcard
    WHERE wiki_articles_fts MATCH '"ancient rom*" OR greec*'
    
    -- Ricerca con NEAR operator (parole vicine entro N parole)
    WHERE wiki_articles_fts MATCH 'pizza NEAR/5 naples'  -- trova pizza a distanza max 5 parole da naples
    
    -- Ricerca con AND implicito (devono essere presenti entrambi i termini)
    WHERE wiki_articles_fts MATCH 'leonardo vinci'
    
    -- Ricerca con AND esplicito
    WHERE wiki_articles_fts MATCH 'leonardo AND vinci'
    
    -- Ricerca con NOT (esclude i risultati con il termine specificato)
    WHERE wiki_articles_fts MATCH 'pyramid NOT egypt'
    
    -- Combinazione complessa
    WHERE wiki_articles_fts MATCH '(archaeolog* OR excavat*) AND rome* NOT (paris OR london)'
    
    -- Ricerca su colonne specifiche (se hai configurato FTS con colonne multiple)
    WHERE wiki_articles_fts MATCH 'name:shakespeare abstract_text:tragedy'

## Soluzione con Embedding

Già implementata negli UltraServices con esempi e test su differenti embedders.

## Soluzione con LLM


## TODO

- [ ] SqliteService chiama un init come Database initializr, duplicato. Se lo togli rompe l'slite connector però.
- [ ] Risolvere il problema che nella query mette i ' e rompe la ricerca

# Credits

* [WikiLite](https://github.com/eja/wikilite) by [Eja](https://eja.it)
* Safe Icon by [Icon 8](https://icons8.com/icon/80779/safe)
