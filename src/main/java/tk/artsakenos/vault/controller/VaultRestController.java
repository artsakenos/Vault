package tk.artsakenos.vault.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.artsakenos.vault.model.UserData;
import tk.artsakenos.vault.service.AIService;
import tk.artsakenos.vault.service.LogService;
import tk.artsakenos.vault.service.ParserService;
import tk.artsakenos.vault.service.SqliteService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/vault")
@Slf4j
public class VaultRestController {

    @Autowired
    private AIService aiService;

    @Autowired
    private SqliteService sqliteService;

    @Autowired
    private ParserService parserService;

    @Autowired
    private LogService logService;

    /**
     * Test the Query to Keywords conversion
     *
     * @param query the query to convert
     * @return the keywords
     */
    @GetMapping("/query2keywords")
    public String convertQueryToKeywords(@RequestParam String query) {
        return aiService.retrieveKeywords(query);
    }

    /**
     * Query the vault
     *
     * @param query the query to search for
     * @return the results
     */
    @GetMapping("/query")
    public List<Map<String, Object>> queryVault(@RequestParam String query) {
        String ftsKeywords = aiService.retrieveKeywords(query);
        log.info("User Query: {}; translated to match clause: {};", query, ftsKeywords);
        return sqliteService.queryDbFts(ftsKeywords);
    }

    @GetMapping("/parse_dir")
    public String parseDir(@RequestParam String dirPath) throws IOException {
        parserService.parseDir(dirPath);
        return "DONE";
    }

    @GetMapping("/parse_file")
    public String parseFile(@RequestParam String filePath) throws IOException {
        parserService.parseFile(filePath);
        return "DONE";
    }


    @GetMapping("/logs")
    public List<UserData> getLogs() {
        return logService.getLastLogs();
    }


}