# Vault Project

One day they might take away our internet because they'll deem it offensive to minorities. 
Then they might take away our electricity - or maybe they won't take it away, 
but they'll jack up the prices using excuses like uncontrollable flu, mask taxes and wheeled desk fees.

Then they might take away our homes - who knows, maybe they'll decide that even to live in our own houses
we'll have to pay rent, disguised as property tax, waste tax, service tax, 
protection money to the neighborhood boss, and kickbacks to the politician in charge.

Then they'll take away our books, claiming emotions are harmful. 
They'll block Wikipedia because someone maybe reported events that never happened 
in a large square in some asian's country capital.

At that point, you'll wish you had a manual teaching you how to start a fire and grow potatoes. 
And why not, to read novels and learn about past cultures, 
so you can share and pass them on to new generations living in a world of plastic and rotten mattresses.

This project is an attempt to create a digital survival manual - a Knowledge Vault that can be consulted offline, 
in a world where basic services are no longer guaranteed.
The target is to store it in an energetic independent device, or a wearable wrist pip boy.

# Repository

This project is primarily source-agnostic, 
the data architecture reflects a compromise between readability and performance. 
You chose your own sources and even personal documents.
However, it's designed to natively import information from several semantic databases and crowd-sourced repositories:

* Wikipedia - The project stores projection of information from Wikipedia the dumps.
  * Check some [Wikipedia](./docs/wikipedia.md) under the hood Features
  * A WIP Study on [Wikipedia Processing](./docs/wikipedia_processing.md)
* OSM - Open Street Maps
  * A WIP Study on [OSM](./docs/openstreetmap.md)
* WikiHow - How to do anything
* ...

# Technologies

It's a [Spring Boot](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.4.1&packaging=jar&jvmVersion=17&groupId=tk.artsakenos&artifactId=vault&name=Vault&description=A%20Vault%20with%20Knowledge&packageName=tk.artsakenos.vault&dependencies=web,lombok,devtools,thymeleaf,data-jpa,h2,spring-ai-ollama) project.

The Vault Database is a SQLite DB because it's lightweight, easy to handle, serverless, 
and it doesn't need to rely on parallelism. 
It's detached from JPA to keep it standalone and simplify custom querying functionality integration.
An H2 database is used within JPA for logging and storing configurations.
It has Full-Text Search (FTS) and vector capabilities.

## Vault Database

* The vault db structure is described [here](./docs/db_structure.sql) together with FTS tables and triggers.
* You can see some [sample FTS queries](./docs/valut_fts_queries.sql).

The structure is scalable, designed to allow the addition of related information without affecting the main tables.
It is incremental, meaning that related information can be added as needed, 
such as processing new metadata, adding new embeddings, and so on.


## Setup
* Note: the following steps need to be automated.
* Download a dump, for example:
  * cd db/dumps/dump_simplewiki
  * wget https://dumps.wikimedia.org/other/enterprise_html/runs/20241201/simplewiki-NS0-20241201-ENTERPRISE-HTML.json.tar.gz
  * tar -xzvf ...
* Parse the directory: http://localhost:8181/vault/parse_dir?dirPath=./db/dump_simplewiki
* Or parse a file: http://localhost:8181/vault/parse_file?filePath=./db/dump_simplewiki/simplewiki_namespace_0_5.ndjson

## Usage
* Perform a search, e.g., http://localhost:8181/vault/search?query=mazzini

[![Watch the Demo](https://img.youtube.com/vi/m3wewJOdCUs/0.jpg)](https://www.youtube.com/watch?v=m3wewJOdCUs&ab_channel=AndreaAddis)

# Credits

* [WikiLite](https://github.com/eja/wikilite) by [Eja](https://eja.it)
* Safe Icon by [Icon 8](https://icons8.com/icon/80779/safe)
