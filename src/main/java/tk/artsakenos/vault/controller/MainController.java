package tk.artsakenos.vault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.artsakenos.vault.service.SqliteService;

import java.sql.SQLException;

@SuppressWarnings("unused")
@Controller
public class MainController {

    @Autowired
    private SqliteService sqliteService;

    @GetMapping("/")
    public String about() {
        return "about";
    }

    @GetMapping("/article_html/{source}/{id}")
    @ResponseBody
    public String showArticleBody(@PathVariable String source, @PathVariable String id) throws SQLException {
        return sqliteService.getArticleHtml(source, id);
    }
}
