package tk.artsakenos.vault.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class TextFileParser {

    private final Path filePath;

    public TextFileParser(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads the file line by line and processes each line.
     */
    public void parseFile() throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(this::processLine);
        }
    }

    /**
     * Abstract method to process a single line. Implement your logic here.
     *
     * @param line the line to process
     */
    protected abstract void processLine(String line);

    public static String getProperty(JsonNode jsonNode, String propertyName) {
        if (jsonNode.at(propertyName) == null) return "";
        return jsonNode.at(propertyName).asText();
    }

    public static ArrayList<String> getPropertyCategories(JsonNode jsonNode) {
        ArrayNode categoriesNode = (ArrayNode) jsonNode.get("categories");
        ArrayList<String> categoryNames = new ArrayList<>();
        if (categoriesNode == null) return categoryNames;
        for (JsonNode category : categoriesNode) {
            String categoryName = category.get("name").asText();
            categoryNames.add(categoryName.substring(categoryName.indexOf(":") + 1));
        }
        return categoryNames;
    }

    public static ArrayList<String> getPropertyTags(JsonNode jsonNode) {
        ArrayNode tagsNode = (ArrayNode) jsonNode.get("tags");
        ArrayList<String> tagNames = new ArrayList<>();
        if (tagsNode == null) return tagNames;
        for (JsonNode category : tagsNode) {
            String categoryName = category.asText();
            tagNames.add(categoryName);
        }
        return tagNames;
    }
}
