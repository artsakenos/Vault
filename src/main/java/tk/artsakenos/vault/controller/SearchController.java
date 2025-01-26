package tk.artsakenos.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String performSearch(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "source", required = false) String source,
            Model model, HttpServletRequest request) {

        // Get the logged-in user's information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName(); // Fetches the username of the logged-in user
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_UNKNOWN"); // Fetches the user's role

        logService.logUserData(request, userName, userRole, query);
        model.addAttribute("sources", sqliteService.getAvailableSources());
        model.addAttribute("source_selected", source);
        model.addAttribute("userName", userName);
        if (query == null || query.trim().isEmpty()) {
            return "search";
        }

        String ftsKeywords = aiService.retrieveKeywords(query);
        if (ftsKeywords == null || ftsKeywords.trim().isEmpty()) {
            ftsKeywords = query;
        } else {
            ftsKeywords = ftsKeywords.replaceAll("'", "\"");
            // ftsKeywords += " AND article_source:scwiki";
        }
        if (!source.trim().isEmpty()) {
            ftsKeywords += " AND article_source:" + source;
        }

        log.info("User Query: {}; translated to match clause: {};", query, ftsKeywords);
        chronometer.start();
        List<Map<String, Object>> results = sqliteService.queryVault(ftsKeywords);
        long duration = chronometer.getTimePassedMillisecs();
        model.addAttribute("query", query);
        model.addAttribute("results", results);
        model.addAttribute("duration", duration);
        model.addAttribute("ftsKeywords", ftsKeywords);
        return "search";
    }


}