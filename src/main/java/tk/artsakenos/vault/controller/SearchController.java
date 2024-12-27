package tk.artsakenos.vault.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.artsakenos.vault.service.AIService;
import tk.artsakenos.vault.service.SqliteService;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
@Slf4j
public class SearchController {

    @Autowired
    private AIService aiService;

    @Autowired
    private SqliteService sqliteService;

    @GetMapping("/search")
    public String performSearch(@RequestParam(value = "query", required = false) String query, Model model) {
        // Se la query è null o vuota, mostra la pagina di ricerca senza risultati
        if (query == null || query.trim().isEmpty()) {
            return "search";
        }

        // Procedi con la ricerca solo se c'è una query
        String ftsKeywords = aiService.retrieveKeywords(query);
        ftsKeywords = ftsKeywords.replaceAll("'", "\"");
        log.info("User Query: {}; translated to match clause: {};", query, ftsKeywords);
        List<Map<String, Object>> results = sqliteService.queryDbFts(ftsKeywords);

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        return "search";
    }


}