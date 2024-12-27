package tk.artsakenos.vault.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.artsakenos.iperunits.database.SQLiteConnector;
import tk.artsakenos.vault.libraries.Helper;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Service
@Slf4j
public class SqliteService {

    @Value("${vault.sqlite_path}")
    private String dbPath;

    @Getter
    private SQLiteConnector db;

    @Autowired
    private AIService aiService;

    private final String ftsQuery = """
            SELECT
              w.id,
              w.name,
              w.abstract_text,
              bm25(wiki_articles_fts) as score,
              rank
            FROM wiki_articles w
            INNER JOIN wiki_articles_fts ON w.id = wiki_articles_fts.rowid
            WHERE wiki_articles_fts MATCH 'MATCH_FTS_CLAUSE'
            ORDER BY rank ASC
            LIMIT 10""";

    @PostConstruct
    public void initializeDatabase() throws SQLException {
        File dbFile = new File(dbPath);
        db = new SQLiteConnector(dbPath);
        if (!dbFile.exists()) {
            db = new SQLiteConnector(dbPath);
            log.info("Database {} not found, I'm initializing it.", dbPath);
            String sqlInit = Helper.getFromResources("/database/init.sql");
            String[] sqlStatements = sqlInit.split("\n\n");
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    db.update(sql);
                }
            }
        }
    }

    public List<Map<String, Object>> queryDbFts(String matchClause) {
        String ftsQuery = this.ftsQuery.replaceAll("MATCH_FTS_CLAUSE", matchClause);
        try {
            return db.query(ftsQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void inserArticle(int identifier, String name, String abstract_text,
                             String articleWiki, String articleHtml, String articleText,
                             String imageUrl, String languageId, String wikiSourceId, String mainEntityId,
                             ArrayList<String> propertyCategories, ArrayList<String> propertyTags) {
        // Insert into DB
        try {
            String insertArticleSQL = """
                        INSERT OR REPLACE INTO wiki_articles (
                            id,
                            name,
                            abstract_text,
                            body_wiki,
                            body_html,
                            body_text,
                            image_url,
                            language_id,
                            wiki_id,
                            entity_id
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            int articleResult = getDb().update(
                    insertArticleSQL,
                    identifier,       // id
                    name,             // name
                    abstract_text,    // abstract_text
                    articleWiki,      // body_wiki
                    articleHtml,      // body_html
                    articleText,      // body_html
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
                boolean categoryTransactionSuccess = getDb().executeTransaction(connection -> {
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
                boolean tagTransactionSuccess = getDb().executeTransaction(connection -> {
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
    }


}
