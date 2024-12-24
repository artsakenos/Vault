package tk.artsakenos.vault.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import tk.artsakenos.iperunits.database.SQLiteConnector;
import tk.artsakenos.iperunits.serial.Jsonable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@SuppressWarnings({"unused", "resource"})
@Slf4j
public class Parser {

    public static int counter = 0;
    public static int rejected = 0;

    @Value("${vault.sqlite_path}")
    private String dbPath;

    public void parse(String filePath) throws IOException {

        SQLiteConnector db = new SQLiteConnector("./db/wiki_lite/wiki_lite.db");
        Path path = Path.of(filePath);
        TextFileParser parser = new TextFileParser(path) {
            @Override
            protected void processLine(String line) {
                // if (++counter > 100) return;

                JsonNode jsonNode = Jsonable.toJsonNode(line);

                int identifier = Integer.parseInt(TextFileParser.getProperty(jsonNode, "/identifier"));
                String mainEntityId = TextFileParser.getProperty(jsonNode, "/main_entity/identifier");
                String abstract_text = TextFileParser.getProperty(jsonNode, "/abstract");
                String name = TextFileParser.getProperty(jsonNode, "/name");

                ArrayList<String> propertyCategories = TextFileParser.getPropertyCategories(jsonNode);
                if (propertyCategories.isEmpty()) {
                    log.warn("No categories found for entity ({}/{}): {}, - {}", identifier, mainEntityId, name, abstract_text);
                    ++rejected;
                    return;
                }
                ArrayList<String> propertyTags = TextFileParser.getPropertyTags(jsonNode);
                if (propertyTags.contains("very short new article")) {
                    log.warn("No interesting Tags found for entity ({}/{}): {}, - {}", identifier, mainEntityId, name, abstract_text);
                    ++rejected;
                    return;
                }

                String imageUrl = TextFileParser.getProperty(jsonNode, "/image/content_url");
                String articleHtml = TextFileParser.getProperty(jsonNode, "/article_body/html");
                String articleWiki = TextFileParser.getProperty(jsonNode, "/article_body/wikiText");
                String languageId = TextFileParser.getProperty(jsonNode, "/in_language/identifier");
                String wikiSourceId = TextFileParser.getProperty(jsonNode, "/is_part_of/identifier");

                // Insert into DB
                try {
                    String insertArticleSQL = """
                                INSERT OR REPLACE INTO wiki_articles (
                                    id,
                                    name,
                                    abstract_text,
                                    body_text,
                                    body_html,
                                    image_url,
                                    language_id,
                                    wiki_id,
                                    entity_id
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """;

                    int articleResult = db.update(
                            insertArticleSQL,
                            identifier,       // id
                            name,             // name
                            abstract_text,    // abstract_text
                            articleWiki,      // body_text
                            articleHtml,      // body_html
                            imageUrl,         // image_url
                            languageId,       // language_id
                            wikiSourceId,     // wiki_id
                            mainEntityId      // entity_id
                    );

                    if (articleResult == 0) {
                        log.warn("Failed to insert or update article: {}, - {}", identifier, name);
                        return;
                    }

                    // Inserimento nella tabella wiki_article_categories
                    if (!propertyCategories.isEmpty()) {
                        boolean categoryTransactionSuccess = db.executeTransaction(connection -> {
                            try (var stmt = connection.prepareStatement(
                                    "INSERT OR IGNORE INTO wiki_article_categories (article_id, category) VALUES (?, ?)"
                            )) {
                                for (String category : propertyCategories) {
                                    stmt.setInt(1, identifier);  // article_id
                                    stmt.setString(2, category); // category
                                    stmt.executeUpdate();
                                }
                            }
                        });

                        if (!categoryTransactionSuccess) {
                            log.warn("Failed to insert categories for article: {}, - {}", identifier, name);
                        }
                    }

                    // Inserimento nella tabella wiki_article_tags
                    if (!propertyTags.isEmpty()) {
                        boolean tagTransactionSuccess = db.executeTransaction(connection -> {
                            try (var stmt = connection.prepareStatement(
                                    "INSERT OR IGNORE INTO wiki_article_tags (article_id, tag) VALUES (?, ?)"
                            )) {
                                for (String tag : propertyTags) {
                                    stmt.setInt(1, identifier);  // article_id
                                    stmt.setString(2, tag);      // tag
                                    stmt.executeUpdate();
                                }
                            }
                        });

                        if (!tagTransactionSuccess) {
                            log.warn("Failed to insert tags for article: {}, - {}", identifier, name);
                        }
                    }

                    // log.info("Successfully inserted article and related data for: {} - {}", identifier, name);

                } catch (Exception e) {
                    log.error("Error while inserting data into the database: {}", e.getMessage(), e);
                }

                System.out.print(".");
                if (counter % 200 == 0) System.out.println(" " + counter);
            }
        };

        parser.parseFile();
    }

    public static void main(String[] args) throws IOException {

        Parser parser = new Parser();
        parser.parse("./db/dump_simplewiki/simplewiki_namespace_0_0.ndjson");
    }

}
