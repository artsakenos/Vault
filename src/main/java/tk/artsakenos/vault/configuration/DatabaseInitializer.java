package tk.artsakenos.vault.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.artsakenos.iperunits.database.SQLiteConnector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@SuppressWarnings({"unused", "resource"})
@Component
@Slf4j
public class DatabaseInitializer {

    @Value("${vault.sqlite_path}")
    private String dbPath;

    @PostConstruct
    public void initializeDatabase() throws IOException, SQLException {
        File dbFile = new File(dbPath);
        SQLiteConnector db = new SQLiteConnector(dbPath);
        if (dbFile.exists()) {
            log.info("Opening Database {}", dbPath);
            db = new SQLiteConnector(dbPath);
            return;
        }

        log.info("Database not found {}, initializing", dbPath);

        InputStream is = getClass().getResourceAsStream("/database/init.sql");
        assert is != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        StringBuilder sqlBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sqlBuilder.append(line).append("\n");
        }

        String[] sqlStatements = sqlBuilder.toString().split("\n\n");
        for (String sql : sqlStatements) {
            sql = sql.trim();
            if (sql.isEmpty()) {
                continue;
            }
            db.update(sql);
        }
    }
}