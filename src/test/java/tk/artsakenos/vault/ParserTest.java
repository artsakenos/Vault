package tk.artsakenos.vault;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tk.artsakenos.vault.libraries.Helper;
import tk.artsakenos.vault.service.ParserService;

import java.io.IOException;

@Disabled
@SpringBootTest
public class ParserTest {

    @Autowired
    private ParserService parser;

    @Test
    void testParse() throws IOException {
        parser.parseFile("./db/dump_simplewiki/simplewiki_namespace_0_6.ndjson");
    }

    @Test
    void testParseDir() throws IOException {
        parser.parseDir("./db/dump_scwiki");
    }

    /**
     * Check per vedere se recupera il file dalle risorse del main.
     */
    @Test
    void testPrompt() {
        String fromResources = Helper.getFromResources("/prompts/prompt_keword_extractor.txt");
        System.out.println(fromResources);
    }

}
