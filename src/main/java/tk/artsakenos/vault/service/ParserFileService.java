package tk.artsakenos.vault.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.artsakenos.vault.model.Article;

import java.nio.file.Path;

@SuppressWarnings({"unused"})
@Slf4j
@Service
public class ParserFileService {

    @Autowired
    private SqliteService sqliteService;

    public void parseFile(Path path) {
        if (path.endsWith(".txt")) {
            Article article = new Article(
                    Article.SOURCE_TEXT, path.getFileName().toString(), path.getFileName().toString(),
                    "", null, null, null, null);
            sqliteService.insert(article);
        }
    }

}
