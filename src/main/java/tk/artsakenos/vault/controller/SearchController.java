package tk.artsakenos.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.artsakenos.iperunits.system.Chronometer;
import tk.artsakenos.vault.service.AIService;
import tk.artsakenos.vault.service.LogService;
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

    @Autowired
    private LogService logService;

    private final Chronometer chronometer = new Chronometer();


    @GetMapping("/search")
    public String performSearch(@RequestParam(value = "query", required = false) String query,
                                Model model, HttpServletRequest request) {
        logService.logUserData(request, query);
        if (query == null || query.trim().isEmpty()) {
            return "search";
        }

        String ftsKeywords = aiService.retrieveKeywords(query);
        ftsKeywords = ftsKeywords.replaceAll("'", "\"");
        log.info("User Query: {}; translated to match clause: {};", query, ftsKeywords);
        chronometer.start();
        List<Map<String, Object>> results = sqliteService.queryDbFts(ftsKeywords);
        long duration = chronometer.getTimePassedMillisecs();

        // Aggiungi i risultati e la query al modello
        model.addAttribute("query", query);
        model.addAttribute("results", results);
        model.addAttribute("duration", duration);

        return "search";
    }


}