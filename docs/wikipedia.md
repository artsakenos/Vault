
Wikipedia è un progetto di enciclopedia online, multilingue, libero e gratuito, scritto collaborativamente da volontari.

# API

[Wikipedia API SandBox](https://en.wikipedia.org/wiki/Special:ApiSandbox)


# Dumps

Wikimedia mette a disposizione dei [dump](https://dumps.wikimedia.org/backup-index.html). 
Contengono i backup dei vari progetti, wikipedia, wikiquote, wikvoyage, ...
Basta scegliere i dump relativi alla lingua selezionata. Ad esempio, per la lingua cinese (zh):
* zhwiki - Wikipedia
* zhwiktionary - Dictionary
* zhwikisource - WikiSource
* zhwikibooks - Books
* ...

Apriamo l'ultimo [zhwiki](https://dumps.wikimedia.org/zhwiki/20250101/), controllando che il dump sia *complete*.

## Dump Strutturati

Per il vault Vengono usati dei dump 
    in un [altro formato](https://dumps.wikimedia.org/other/) 
    in particolare, un [formato json già ben strutturato](https://dumps.wikimedia.org/other/enterprise_html/).
Da qui cercare la [directory](https://dumps.wikimedia.org/other/enterprise_html/runs/20250101/)
Tenendo conto che, ad esempio, in *zhwiki-NS0-20250101-ENTERPRISE-HTML.json.tar.gz*:
* zh: Codice ISO 639-1 per la lingua cinese.
* wiki indica che è il dump di un progetto Wikipedia.
* NS0: Identifica lo spazio dei nomi (namespace) all'interno del progetto.
  * **NS0** (quello che serve) si riferisce agli articoli principali o pagine di contenuto standard di Wikipedia.
  * NS1: Discussioni sugli articoli.
  * NS4: Wikipedia (pagine relative al progetto stesso).
  * NS8: MediaWiki (pagine di sistema per l'interfaccia utente).
  * NS10: Template riutilizzabili.
  * NS14: Categorie per organizzare pagine.
  * NS6: File multimediali con dettagli e licenze.  * 
* 20241201: Data del dump.
* ENTERPRISE-HTML: Specifica che il file contiene dati HTML pre-elaborati dal servizio Enterprise.
* .json formato, e .tar.gz compressione.


Alcuni dump di esempio:
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
* P569 È la data di nascita datatype": "time" → "datavalue": { "value": { "time": "+1807-07-04T00:00:00Z",
* P119 È il luogo di sepoltura, datatype: "wikibase-item" → punta a Q845310 (Caprera) → "datavalue": { "value": { "id": "Q845310" } }

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

