package tk.artsakenos.vault.libraries;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
@Slf4j
public class Helper {

    @SneakyThrows
    public static String getFromResources(String path) {
        InputStream is = Helper.class.getResourceAsStream(path);
        if (is == null) {
            log.error("Path Not found in resources: {}.", path);
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        StringBuilder sqlBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sqlBuilder.append(line).append("\n");
        }
        return sqlBuilder.toString();
    }

    public static String jsonizeString(String input) {
        return input
                .replace("\r", "")
                .replace("\n", "\\\\n")
                .replace("\t", "\\\\t")
                .replace("\"", "\\'")
                .trim();
    }

}
