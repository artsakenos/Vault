package tk.artsakenos.vault;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tk.artsakenos.vault.libraries.EmbeddingModel;
import tk.artsakenos.vault.libraries.Helper;
import tk.artsakenos.vault.service.EmbeddingService;
import tk.artsakenos.vault.service.ParserWikiService;

import java.io.IOException;
import java.sql.SQLException;

@Disabled
@SpringBootTest
public class ParserTest {

    @Autowired
    private ParserWikiService parser;

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void testParse() throws IOException {
        parser.parseFile("./db/dump_simplewiki/simplewiki_namespace_0_6.ndjson");
    }

    @Test
    void testParseDir() throws IOException {
        parser.parseDir("./db/dumps/dump_scwiki");
    }

    /**
     * Check per vedere se recupera il file dalle risorse del main.
     */
    @Test
    void testPrompt() {
        String fromResources = Helper.getFromResources("/prompts/prompt_keword_extractor.txt");
        System.out.println(fromResources);
    }

    @Test
    void testAddEmbeddings() throws SQLException, TranslateException, ModelNotFoundException, MalformedModelException, IOException {
        embeddingService.addEmbeddings(EmbeddingModel.MODEL_DEFAULT.getName());
    }

}
