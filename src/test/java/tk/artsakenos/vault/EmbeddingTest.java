package tk.artsakenos.vault;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tk.artsakenos.vault.libraries.EmbeddingHelper;
import tk.artsakenos.vault.libraries.EmbeddingModel;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
@Slf4j
public class EmbeddingTest {

    /**
     * Nota che la distanza tra i dui "cuisine" dovrebbe sempre essere inferiore a distanza tra cuisine e saluti per dire.
     */
    @Test
    void testEmbedding() {

        float[] embeddings_cuisine01 = EmbeddingHelper.getEmbeddings(EmbeddingModel.MODEL_ITALIAN.getName(), "Pizza alla Margherita, Pizza all'Ananas, Pollo con Calamaretti.");
        float[] embeddings_cuisine02 = EmbeddingHelper.getEmbeddings(EmbeddingModel.MODEL_ITALIAN.getName(), "Maccaroni, Pasta, Pizzette, e si pulisce anche da solo sforna tutto!");
        float[] embeddings_saluto03 = EmbeddingHelper.getEmbeddings(EmbeddingModel.MODEL_ITALIAN.getName(), "Ciao, come stai?");
        log.info("Embedding for {}: {}", EmbeddingModel.MODEL_ITALIAN, embeddings_cuisine01);

        float distance_cuisine_ita = EmbeddingHelper.distance(embeddings_cuisine01, embeddings_cuisine02);
        log.info("Distanza Cuisine (Italian): {}", distance_cuisine_ita);
        log.info("Distanza Cuisine/Saluti (Italian): {}", EmbeddingHelper.distance(embeddings_cuisine01, embeddings_saluto03));

        embeddings_cuisine01 = EmbeddingHelper.getEmbeddings(EmbeddingModel.MODEL_MULTILINGUAL.getName(), "Pizza alla Margherita, Pizza all'Ananas, Pollo con Calamaretti.");
        embeddings_cuisine02 = EmbeddingHelper.getEmbeddings(EmbeddingModel.MODEL_MULTILINGUAL.getName(), "Maccaroni, Pasta, Pizzette, e si pulisce anche da solo sforna tutto!");
        embeddings_saluto03 = EmbeddingHelper.getEmbeddings(EmbeddingModel.MODEL_MULTILINGUAL.getName(), "Ciao, come stai?");
        log.info("Embedding for {}: {}", EmbeddingModel.MODEL_MULTILINGUAL, embeddings_cuisine01);

        float distance_cuisine_multi = EmbeddingHelper.distance(embeddings_cuisine01, embeddings_cuisine02);
        log.info("Distanza Cuisine (Multilingual): {}", distance_cuisine_multi);
        log.info("Distanza Cuisine/Saluti (Multilingual): {}", EmbeddingHelper.distance(embeddings_cuisine01, embeddings_saluto03));

        try {
            byte[] ec_blob01 = EmbeddingHelper.embeddingsToBlob(embeddings_cuisine01);
            byte[] ec_blob02 = EmbeddingHelper.embeddingsToBlob(embeddings_cuisine02);
            float distance_cuisine_multi_blob = EmbeddingHelper.distance(ec_blob01, ec_blob02);
            log.info("Distanza Cuisine (Multilingual BLOB): {}", distance_cuisine_multi_blob);

            // Assertions
            assertTrue(distance_cuisine_ita > distance_cuisine_multi, "La distanza tra le cuisine in italiano deve essere maggiore di quella in multilingua");
            assertEquals(distance_cuisine_multi, distance_cuisine_multi_blob, 1e-6, "Le distanze in multilingua (float vs BLOB) devono essere uguali");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
