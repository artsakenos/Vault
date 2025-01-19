package tk.artsakenos.vault.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.artsakenos.iperunits.file.FileManager;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.vault.model.Article;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;

@SuppressWarnings({"unused"})
@Slf4j
@Service
public class ParserWikiService {

    @Autowired
    private SqliteService sqlite;

    @Getter
    private String parserStatus = "#idle";

    public void parseDir(String dirPath) throws IOException {
        String[] files = FileManager.getFiles(dirPath, false, new String[]{".ndjson"});
        for (String file : files) {
            parseFile(dirPath + "/" + file);
        }
    }

    public void parseFile(String filePath) throws IOException {
        final int[] counter = {0, 0}; // Parsed, Rejected
        log.info("Parsing File {}.", filePath);
        Path path = Path.of(filePath);
        WikiJsonDumpParser parser = new WikiJsonDumpParser(path) {
            @Override
            protected void processLine(String line) {
                if (parseLine(line)) ++counter[0];
                else ++counter[1];
                // if (counter[1] > 100) return;

                if (++counter[0] % 10 == 0) System.out.print("➤");
                if (counter[0] % 1000 == 0) System.out.println(" C" + counter[0] + "/R" + counter[1]);
                parserStatus = "#inprogress. Parsing in progress for " + path + "\n" + " C" + counter[0] + "/R" + counter[1];
            }
        };

        parser.parseFile();
        log.info("Total articles processed: {}. Article Rejected: {}", counter[0], counter[1]);
        parserStatus = "#idle. Parsing completed at " + Instant.now() + " for " + path + "\n" + " C" + counter[0] + "/R" + counter[1];
    }

    private boolean parseLine(String line) {
        JsonNode jsonNode = Jsonable.toJsonNode(line);
        Article article = new Article();
        article.setSource(Article.SOURCE_WIKI);
        String identifier = WikiJsonDumpParser.getProperty(jsonNode, "/identifier");

        article.setId(identifier);
        article.addMetadata(Article.METADATA_ENTITY_ID, WikiJsonDumpParser.getProperty(jsonNode, "/main_entity/identifier"));
        article.setDescription(WikiJsonDumpParser.getProperty(jsonNode, "/abstract"));
        article.setName(WikiJsonDumpParser.getProperty(jsonNode, "/name"));

        ArrayList<String> propertyCategories = WikiJsonDumpParser.getPropertyCategories(jsonNode);
        if (propertyCategories.isEmpty()) {
            log.warn("No Categories found for {}): {}, - {}", identifier, article.getName(), article.getDescription());
            return false;
        }
        article.setCategories(propertyCategories);
        ArrayList<String> propertyTags = WikiJsonDumpParser.getPropertyTags(jsonNode);
        if (propertyTags.contains("very short new article")) {
            log.warn("No Tags found for {}): {}, - {}", identifier, article.getName(), article.getDescription());
            return false;
        }
        article.setTags(propertyTags);

        article.addMetadata(Article.METADATA_LANGUAGE, WikiJsonDumpParser.getProperty(jsonNode, "/in_language/identifier"));
        article.addMetadata(Article.METADATA_WIKI_SOURCE, WikiJsonDumpParser.getProperty(jsonNode, "/is_part_of/identifier"));
        article.addMetadata(Article.METADATA_BANNER, WikiJsonDumpParser.getProperty(jsonNode, "/image/content_url"));

        String articleHtml = WikiJsonDumpParser.getProperty(jsonNode, "/article_body/html");
        String articleWiki = WikiJsonDumpParser.getProperty(jsonNode, "/article_body/wikitext");
        String articleText = Jsoup.parse(articleHtml).body().text();

        // Non è necessario, un trigger indicizza l'article description
        // article.addChunk(Article.CHUNK_ABSTRACT, null, 1, 1, article.getDescription(), null);
        article.addChunk(Article.CHUNK_HTML, null, 1, 1, articleHtml, null);
        article.addChunk(Article.CHUNK_MARKDOWN, null, 1, 1, articleWiki, null);
        article.addChunk(Article.CHUNK_TEXT, null, 1, 1, articleText, null);

        chunkMarkdown(article, articleWiki);

        sqlite.insert(article);
        return true;
    }

    private void chunkMarkdown(Article article, String articleWiki) {
        String[] wikiSections = articleWiki.replaceAll("\r", "").split("\n==");
        int counter = 0;
        int total = wikiSections.length;
        for (String wikiSection : wikiSections) {
            int sStart = wikiSection.indexOf(" =") + 2;
            int sEnd = wikiSection.indexOf(" =", sStart);
            String sectionName = wikiSection.substring(sStart, sEnd);
            article.addChunk(Article.CHUNK_MARKDOWN, sectionName, ++counter, total, wikiSection, null);
        }

    }


}
