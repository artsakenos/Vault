package tk.artsakenos.vault.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.artsakenos.vault.libraries.EmbeddingHelper;
import tk.artsakenos.vault.libraries.Helper;
import tk.artsakenos.vault.libraries.SQLiteWrapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static tk.artsakenos.vault.libraries.EmbeddingHelper.blobToEmbeddings;
import static tk.artsakenos.vault.libraries.EmbeddingHelper.embeddingsToBlob;

@SuppressWarnings("unused")
@Service
@Slf4j
public class EmbeddingService {

    @Autowired
    private SqliteService sqlite;

    /**
     * This method takes the chunks from the db without the embeddings and adds them.
     *
     * @param embeddingModel the model to use for the embeddings
     */
    public void addEmbeddings(String embeddingModel) throws SQLException, IOException {
        log.info("Adding embeddings: {}", embeddingModel);
        String sql = Helper.getFromResources("/database/vault_select_chunks_without_embeddings.sql");
        List<Map<String, Object>> result = sqlite.getDb().query(sql, embeddingModel);
        int counter = 0;
        for (Map<String, Object> row : result) {
            String chunkText = row.get("chunk_text").toString();
            float[] embedding = EmbeddingHelper.getEmbeddings(embeddingModel, chunkText);
            byte[] embeddingBlob = embeddingsToBlob(embedding);
            String insertSql = """
                    INSERT OR REPLACE INTO article_embeddings (
                        article_source, article_id, chunk_type, chunk_id, embedding_model, embedding_vector
                    ) VALUES (?, ?, ?, ?, ?, ?);""";
            sqlite.getDb().update(insertSql, row.get("article_source"), row.get("article_id"),
                    row.get("chunk_type"), row.get("chunk_id"), embeddingModel, embeddingBlob);
            if (++counter % 100 == 0) {
                log.info("Added {} embeddings", counter);
            }
        }
    }


    public List<Map<String, Object>> getEmbeddingsByModel(String embeddingModel) throws SQLException, IOException {
        String sql = "SELECT * FROM article_embeddings WHERE embedding_model = ?;";
        try (SQLiteWrapper.QueryResult queryResult = sqlite.getDb().queryRS(sql, embeddingModel)) {
            ResultSet resultSet = queryResult.getResultSet();
            while (resultSet.next()) {
                byte[] embeddingBlob = resultSet.getBytes("embedding_vector");
                float[] embeddingVector = blobToEmbeddings(embeddingBlob);
                System.out.println("DEBUG EMBEDS: " + resultSet.getString("article_id"));
            }
        }
        return null;
    }

}
