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

    @GetMapping("/")
    public String showSearchPage() {
        return "search";
    }

    @GetMapping("/search")
    public String performSearch(@RequestParam("query") String query, Model model) {

        String ftsKeywords = aiService.retrieveKeywords(query);
        log.info("User Query: {}; translated to match clause: {};", query, ftsKeywords);
        List<Map<String, Object>> results = sqliteService.queryDbFts(ftsKeywords);

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        return "search-results";
    }
}