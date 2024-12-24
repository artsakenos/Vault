package tk.artsakenos.vault.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.artsakenos.iperunits.database.SQLiteConnector;
import tk.artsakenos.vault.libraries.Helper;

import java.io.File;
import java.sql.SQLException;

@SuppressWarnings({"unused", "resource"})
@Component
@Slf4j
public class DatabaseInitializer {

    @Value("${vault.sqlite_path}")
    private String dbPath;

    @PostConstruct
    public void initializeDatabase() throws SQLException {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            SQLiteConnector db = new SQLiteConnector(dbPath);
            log.info("Database {} not found, I'm initializing it.", dbPath);
            String sqlInit = Helper.getFromResources("/database/init.sql");
            String[] sqlStatements = sqlInit.split("\n\n");
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    db.update(sql);
                }
            }
        }
    }
}