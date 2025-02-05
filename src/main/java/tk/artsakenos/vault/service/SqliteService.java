package tk.artsakenos.vault.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.artsakenos.vault.libraries.Helper;
import tk.artsakenos.vault.libraries.SQLiteWrapper;
import tk.artsakenos.vault.model.Article;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Slf4j
@Service
public class SqliteService {

    @Value("${vault.sqlite_path}")
    private String dbPath;

    @Getter
    private SQLiteWrapper db;

    @Autowired
    private AIService aiService;

    @PostConstruct
    public void initializeDatabase() throws SQLException {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            // Create the directory for the database if it doesn't exist
            File dbDir = new File(dbFile.getParent());
            if (!dbDir.exists() && !dbDir.mkdirs()) {
                throw new RuntimeException("Failed to create database directory: " + dbDir.getAbsolutePath());
            }
        }
        db = new SQLiteWrapper(dbPath);
        if (!db.tableExists("articles")) {
            log.info("Database {} not initialized, I'm doing it.", dbPath);
            String sqlInit = Helper.getFromResources("/database/vault_init.sql");
            String[] sqlStatements = sqlInit.split("\n\n");
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    db.update(sql);
                }
            }
        }

    }

    public void insert(Article article) {
        // Base article insert - single record, no need for transaction
        String insertArticleSQL = """
                INSERT OR REPLACE INTO articles (source, id, name, description)
                VALUES (?, ?, ?, ?)
                """;

        try {
            db.update(insertArticleSQL,
                    article.getSource(),
                    article.getId(),
                    article.getName(),
                    article.getDescription()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Categories batch insert
        if (article.getCategories() != null && !article.getCategories().isEmpty()) {
            executeTransaction(connection -> {
                try (var stmt = connection.prepareStatement("""
                        INSERT OR REPLACE INTO article_categories (source, article_id, category)
                        VALUES (?, ?, ?)
                        """)) {
                    for (String category : article.getCategories()) {
                        stmt.setString(1, article.getSource());
                        stmt.setString(2, article.getId());
                        stmt.setString(3, category);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            });
        }

        // Tags batch insert
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            executeTransaction(connection -> {
                try (var stmt = connection.prepareStatement("""
                        INSERT OR REPLACE INTO article_tags (source, article_id, tag)
                        VALUES (?, ?, ?)
                        """)) {
                    for (String tag : article.getTags()) {
                        stmt.setString(1, article.getSource());
                        stmt.setString(2, article.getId());
                        stmt.setString(3, tag);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            });
        }

        // Metadata batch insert
        if (article.getMetadata() != null && !article.getMetadata().isEmpty()) {
            executeTransaction(connection -> {
                try (var stmt = connection.prepareStatement("""
                        INSERT OR REPLACE INTO article_meta (source, article_id, meta_type, meta_value)
                        VALUES (?, ?, ?, ?)
                        """)) {
                    for (Map.Entry<String, String> meta : article.getMetadata().entrySet()) {
                        stmt.setString(1, article.getSource());
                        stmt.setString(2, article.getId());
                        stmt.setString(3, meta.getKey());
                        stmt.setString(4, meta.getValue());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            });
        }

        // Chunks batch insert
        if (article.getChunks() != null && !article.getChunks().isEmpty()) {
            // Insert chunks
            executeTransaction(connection -> {
                try (var stmt = connection.prepareStatement("""
                        INSERT OR REPLACE INTO article_chunks
                        (source, article_id, chunk_type, chunk_section, chunk_id, chunk_count, chunk_text)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """)) {
                    for (Article.ArticleChunk chunk : article.getChunks()) {
                        stmt.setString(1, article.getSource());
                        stmt.setString(2, article.getId());
                        stmt.setString(3, chunk.getChunkType());
                        stmt.setString(4, chunk.getChunkSection());
                        stmt.setInt(5, chunk.getChunkId());
                        stmt.setInt(6, chunk.getChunkCount());
                        stmt.setString(7, chunk.getChunkText());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            });

            // Insert embeddings for chunks
            executeTransaction(connection -> {
                try (var stmt = connection.prepareStatement("""
                        INSERT OR REPLACE INTO article_embeddings
                        (source, article_id, chunk_type, chunk_id, embedding_model, embedding_vector)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """)) {
                    for (Article.ArticleChunk chunk : article.getChunks()) {
                        if (chunk.getEmbeddings() != null) {
                            for (Article.ChunkEmbedding embedding : chunk.getEmbeddings()) {
                                stmt.setString(1, article.getSource());
                                stmt.setString(2, article.getId());
                                stmt.setString(3, chunk.getChunkType());
                                stmt.setInt(4, chunk.getChunkId());
                                stmt.setString(5, embedding.getEmbeddingModel());
                                stmt.setBytes(6, embedding.getEmbeddingVector());
                                stmt.addBatch();
                            }
                        }
                    }
                    stmt.executeBatch();
                }
            });
        }
    }


    private void executeTransaction(TransactionCallback callback) {
        Connection connection;
        connection = db.getConnection();
        try {
            connection.setAutoCommit(false);
            callback.doInTransaction(connection);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String getArticleHtml(int id) {
        return "Ciao, Cipollino!";
    }

    @FunctionalInterface
    private interface TransactionCallback {
        void doInTransaction(Connection connection) throws SQLException;
    }

    public List<Map<String, Object>> queryVault(String matchClause) {
        String sqlQuery = Helper.getFromResources("/database/vault_fts_query.sql");
        try {
            return db.query(sqlQuery, matchClause);
        } catch (SQLException e) {
            log.error("Error while querying the vault: {}", e.getLocalizedMessage());
            return null;
        }
    }
}