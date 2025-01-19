package tk.artsakenos.vault.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String source;
    private String id;
    private String name;
    private String description;

    public static final String SOURCE_WIKI = "wiki";
    public static final String SOURCE_PDF = "pdf";
    public static final String SOURCE_TEXT = "text";
    public static final String SOURCE_WEB = "web";

    public static final String METADATA_ENTITY_ID = "ENTITY_ID";
    public static final String METADATA_LANGUAGE = "LANGUAGE";
    public static final String METADATA_WIKI_SOURCE = "WIKI_SOURCE";
    public static final String METADATA_BANNER = "BANNER";

    public static final String CHUNK_ABSTRACT = "ABSTRACT";
    public static final String CHUNK_TEXT = "TEXT";
    public static final String CHUNK_HTML = "HTML";
    public static final String CHUNK_MARKDOWN = "MARKDOWN";

    // Categories and tags
    private List<String> categories = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    // Metadata
    private Map<String, String> metadata = new HashMap<>();

    // Chunks with their embeddings
    private List<ArticleChunk> chunks = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleChunk {
        private String chunkType;
        private String chunkSection;
        private Integer chunkId;
        private Integer chunkCount;
        private String chunkText;
        private List<ChunkEmbedding> embeddings = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChunkEmbedding {
        private String embeddingModel;
        private byte[] embeddingVector;
    }

    public void addMetadata(String key, String value) {
        getMetadata().put(key, value);
    }

    public void addChunk(String chunkType, String chunkSection, int chunkId, int chunkCount, String chunkText, List<ChunkEmbedding> embeddings) {
        getChunks().add(new ArticleChunk(chunkType, chunkSection, chunkId, chunkCount, chunkText, embeddings));
    }
}