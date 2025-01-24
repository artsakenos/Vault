# Vault Project

In a world of uncertainty, we need a proactive approach to knowledge preservation —
a digital sanctuary ensuring information remains accessible, regardless of external constraints.

One day they might take away our internet because they'll deem it offensive to minorities. 
Then they might take away our electricity - or maybe they won't take it away, 
but they'll jack up the prices using excuses like uncontrollable flu, mask taxes and wheeled desk fees.

Then they might take away our homes, or they'll decide that to live in our own houses
we'll have to pay rent, disguised as property tax, waste tax, service tax, gasoline excise duties,
protection money to the neighborhood boss, and kickbacks to the politician in charge.

Then they'll take away our books, claiming emotions are harmful 
and that they care about our well-being, and that they want to protect us under their wild armpit.
They'll block Wikipedia because someone maybe reported events that never happened 
in a large square in some eastern country capital.

At that point, you'll wish you had a manual teaching you how to start a fire and grow potatoes. 
And why not, to read novels and learn about past cultures, 
so you can share and pass them on to new generations living in a world of plastic and rotten beds.

This project is an attempt to create a digital survival manual - a Knowledge Vault that can be consulted offline, 
in a world where basic services are no longer guaranteed.
The target is to store it in an energetic independent device, or a wearable wrist pip boy.

# Repository

This project is primarily source-agnostic, 
the data architecture reflects a compromise between readability and performance. 
You chose your own sources and even personal documents.
However, it's designed to natively import information from several semantic databases and crowd-sourced repositories:

* Wikipedia - The project stores projection of information from the Wikipedia dumps.
  * Check some [Wikipedia](./docs/wikipedia.md) under the hood Features ([In Italian](./docs/wikipedia_it.md))
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

* The vault db structure is described [here](./src/main/resources/database/vault_init.sql) together with FTS tables and triggers.
* You can see some [sample FTS queries](./docs/vault_fts_queries.sql).

The structure is scalable, designed to allow the addition of related information without affecting the main tables.
It is incremental, meaning that related information can be added as needed, 
such as processing new metadata, adding new embeddings, and so on.

# Installation & Usage

1. Clone the repository
2. Setup your src/main/resources/env.properties
3. Compile, and run.

**Wikipedia Dumps Import** - See [Wikipedia Dumps](./docs/wikipedia.md#download-procedure) for more details.

**Search** - Perform a search, e.g., http://localhost:8181/search?query=mazzini

[![Watch the Demo](https://img.youtube.com/vi/m3wewJOdCUs/0.jpg)](https://www.youtube.com/watch?v=m3wewJOdCUs&ab_channel=AndreaAddis)


# TODO

- [ ] Risolvere il problema dei ' nelle query di ricerca
- [ ] Security
- [ ] Implementare vector search
- [ ] Pool of query per pulire il DB da 
  - disambigua WP:DISAMBIG|disambiguation page, ..., {{disambig}} 
  - original markdown? Text?
  - chunk troppo ridotti


# Credits

* [WikiLite](https://github.com/eja/wikilite) by [Eja](https://eja.it)
* Safe Icon by [Icon 8](https://icons8.com/icon/80779/safe)
* LLM Search Powered by [Groq](https://groq.com)
