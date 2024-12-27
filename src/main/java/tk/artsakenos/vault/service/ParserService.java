package tk.artsakenos.vault.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.artsakenos.iperunits.file.FileManager;
import tk.artsakenos.iperunits.serial.Jsonable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@SuppressWarnings({"unused"})
@Slf4j
@Service
public class ParserService {

    public static int counter = 0;
    public static int rejected = 0;

    @Autowired
    private SqliteService sqlite;

    public void parseFile(String filePath) throws IOException {
        log.info("Parsing File {}.", filePath);
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
                String articleWiki = TextFileParser.getProperty(jsonNode, "/article_body/wikitext");
                String languageId = TextFileParser.getProperty(jsonNode, "/in_language/identifier");
                String wikiSourceId = TextFileParser.getProperty(jsonNode, "/is_part_of/identifier");

                Document doc = Jsoup.parse(articleHtml);
                String articleText = doc.body().text();

                sqlite.inserArticle(identifier, name, abstract_text,
                        articleWiki, articleHtml, articleText, imageUrl,
                        languageId, wikiSourceId, mainEntityId,
                        propertyCategories, propertyTags);

                System.out.print("➤");
                if (++counter % 100 == 0) System.out.println(" C" + counter + "/R" + rejected);
            }
        };

        parser.parseFile();
        log.info("Total articles processed: {}. Article Rejected: {}", counter, rejected);
    }

    public void parseDir(String dirPath) throws IOException {
        String[] files = FileManager.getFiles(dirPath, false, new String[]{".ndjson"});
        for (String file : files) {
            parseFile(dirPath + "/" + file);
        }
    }


}
