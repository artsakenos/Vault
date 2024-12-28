package tk.artsakenos.vault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tk.artsakenos.vault.service.SqliteService;

@Controller
public class MainController {

    @Autowired
    private SqliteService sqliteService;

    @GetMapping("/")
    public String about() {
        return "about";
    }


    @GetMapping("article/{id}")
    public String showArticle(@PathVariable int id, Model model) {
        String articleHtml = sqliteService.getArticleHtml(id);
        model.addAttribute("articleHtml", articleHtml);
        return "article";
    }




}
