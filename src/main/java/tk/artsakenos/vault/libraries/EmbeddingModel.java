package tk.artsakenos.vault.libraries;


public enum EmbeddingModel {

    // https://huggingface.co/sentence-transformers?sort_models=likes#models
    MODEL_DEFAULT("sentence-transformers/all-MiniLM-L6-v2"),
    MODEL_MPNET("sentence-transformers/all-mpnet-base-v2"),
    MODEL_MULTILINGUAL("sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"),
    MODEL_ITALIAN("nickprock/sentence-bert-base-italian-uncased"),
    MODEL_BGEM3("BAAI/bge-m3");

    private final String modelName;

    public String getName() {
        return modelName;
    }

    EmbeddingModel(String modelName) {
        this.modelName = modelName;
    }

    public static EmbeddingModel fromModelName(String modelName) {
        for (EmbeddingModel model : EmbeddingModel.values()) {
            if (model.modelName.equals(modelName)) {
                return model;
            }
        }
        throw new IllegalArgumentException("No enum constant with modelName " + modelName);
    }
}