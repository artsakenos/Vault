package tk.artsakenos.vault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.artsakenos.vault.service.AIService;

import java.io.IOException;

@RestController
public class AIRestController {

    @Autowired
    private AIService aiService;

    @GetMapping("/redigiContratto")
    public String redigiContratto(@RequestParam String query) {
        try {
            return aiService.retrieveKeywords(query);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing the request";
        }
    }
}