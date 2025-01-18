package tk.artsakenos.vault.libraries;

import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import lombok.extern.slf4j.Slf4j;
import tk.artsakenos.iperunits.serial.Jsonable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a cache to avoid reloading embeddings models over time.
 */
@SuppressWarnings("unused")
@Slf4j
public class EmbeddingHelper {


    private static final String DJL_PATH = "djl://ai.djl.huggingface.pytorch/";

    // Mappa per memorizzare i modelli e i predictor
    private static final Map<String, ModelPredictorPair> cache = new HashMap<>();

    private static class ModelPredictorPair {
        ZooModel<String, float[]> model;
        Predictor<String, float[]> predictor;

        ModelPredictorPair(ZooModel<String, float[]> model, Predictor<String, float[]> predictor) {
            this.model = model;
            this.predictor = predictor;
        }
    }

    public static float[] getEmbeddings(String modelName, String text) {
        if (text == null || text.isEmpty()) return null;

        try {
            ModelPredictorPair pair = cache.get(modelName);
            if (pair == null) { // If not in cache, lo carico.
                Criteria<String, float[]> criteria = Criteria.builder()
                        .setTypes(String.class, float[].class)
                        .optModelUrls(DJL_PATH + modelName)
                        .optEngine("PyTorch")
                        .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                        .optProgress(new ProgressBar())
                        .build();

                ZooModel<String, float[]> model = criteria.loadModel();
                Predictor<String, float[]> predictor = model.newPredictor();
                pair = new ModelPredictorPair(model, predictor);
                cache.put(modelName, pair);
            }

            return pair.predictor.predict(text);
        } catch (Exception e) {
            log.error("Errore durante il recupero degli embeddings: {}", e.getMessage());
            return null;
        }
    }

    public static void closeCache() {
        for (ModelPredictorPair pair : cache.values()) {
            try {
                pair.predictor.close();
                pair.model.close();
            } catch (Exception e) {
                log.error("Errore durante la chiusura del modello o predictor: {}", e.getMessage());
            }
        }
        cache.clear();
    }

    public static String getEmbeddingsJson(String modelName, String text) {
        float[] embeddings = getEmbeddings(modelName, text);
        return Jsonable.toJson(embeddings, false);
    }

    public static float distance(float[] vector1, float[] vector2) {
        if (vector1 == null || vector2 == null || vector1.length != vector2.length) {
            log.error("Wrong vector lenght");
            return Float.MAX_VALUE;
        }

        float squaredDistance = 0.0f;
        for (int i = 0; i < vector1.length; i++) {
            float diff = vector1[i] - vector2[i];
            squaredDistance += diff * diff;
        }

        return (float) Math.sqrt(squaredDistance);
    }

    public static float distance(byte[] blob1, byte[] blob2) throws IOException {
        if (blob1 == null || blob2 == null || blob1.length != blob2.length) {
            log.error("Wrong blob length or null input");
            return Float.MAX_VALUE;
        }

        ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(blob1);
        DataInputStream dataInputStream1 = new DataInputStream(byteArrayInputStream1);

        ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(blob2);
        DataInputStream dataInputStream2 = new DataInputStream(byteArrayInputStream2);

        float squaredDistance = 0.0f;

        try {
            while (dataInputStream1.available() > 0 && dataInputStream2.available() > 0) {
                float value1 = dataInputStream1.readFloat();
                float value2 = dataInputStream2.readFloat();
                float diff = value1 - value2;
                squaredDistance += diff * diff;
            }
        } catch (IOException e) {
            log.error("Errore durante la lettura dei blob: {}", e.getMessage());
            return Float.MAX_VALUE;
        } finally {
            dataInputStream1.close();
            dataInputStream2.close();
        }

        return (float) Math.sqrt(squaredDistance);
    }


    /**
     * Converts an array of float in an array of byte (BLOB).
     */
    public static byte[] embeddingsToBlob(float[] embedding) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        for (float value : embedding) {
            dataOutputStream.writeFloat(value);
        }
        dataOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Converts an array of byte (BLOB) to an array of float.
     */
    public static float[] blobToEmbeddings(byte[] blob) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(blob);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        float[] embedding = new float[blob.length / 4]; // Ogni float occupa 4 byte
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = dataInputStream.readFloat(); // Leggi ogni valore float
        }
        return embedding;
    }

}
