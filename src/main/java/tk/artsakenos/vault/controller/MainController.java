package tk.artsakenos.vault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.artsakenos.vault.service.SqliteService;

@Controller
public class MainController {

    @Autowired
    private SqliteService sqliteService;

    @GetMapping("/")
    public String about() {
        return "about";
    }


    @GetMapping("article_body/{id}")
    @ResponseBody
    public String showArticleBody(@PathVariable int id) {
        return sqliteService.getArticleHtml(id);
    }
}
