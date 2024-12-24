package tk.artsakenos.vault.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperHttpClient;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unused")
@Service
@Slf4j
public class AIService {

    private SuperHttpClient client;

    @Value("${vault.key_groq_inf}")
    private String GROQ_INFORA;


    public static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    public static final String GROQ_SYSTEM_QUERY_KEYWORD = """
            Data la query che farà l'utente rispondi con un insieme di keyword che permetteranno di fare una ricerca efficace.
            Non dare spiegazioni, rispondi solo con le keywork che servono per effettuare la ricerca e nient'altro.
            Le parole devono essere separate da spazi, con caratteri minuscoli, non ci devono essere segni di interpunzione.
            Importante: Le parole nella risposta devono essere in inglese!.
            """.replaceAll("\n", "\\\\n").trim();
    public static final String GROQ_JSON_WRAPPER = """
            {
                "max_tokens": 1024,
                "messages": [
                    {
                        "content": SYSTEM,
                        "role": "system"
                    },
                    {
                        "content": CONTENT,
                        "role": "user"
                    }
                ],
                "model": "llama-3.1-70b-versatile",
                "stream": false,
                "temperature": 1,
                "top_p": 1
            }
            """;

    @PostConstruct
    public void init() {
        client = new SuperHttpClient(GROQ_URL, null, null);
    }


    public String retrieveKeywords(String query) throws IOException {

        String jsonQuestion = Jsonable.toJson(query.replaceAll("\"", "'"), false);
        String jsonSystem = Jsonable.toJson(GROQ_SYSTEM_QUERY_KEYWORD, false);
        String jsonBody = GROQ_JSON_WRAPPER
                .replaceAll("SYSTEM", jsonSystem)
                .replaceAll("CONTENT", jsonQuestion);

        Map<String, String> postParameters = Map.of(
                "Authorization", "Bearer " + GROQ_INFORA);
        SuperHttpClient.SuperResponse superResponse = client.postJson("", jsonBody, postParameters, null);

        if (!superResponse.isSuccessful()) {
            return "ERROR (" + superResponse.getCode() + "): " + superResponse.getBody();
        }

        String jsonResponse = superResponse.getBody();
        JsonNode rootNode = Jsonable.getJsonNodeByPath(jsonResponse, "");
        // String id = rootNode.get("id").asText();
        // String model = rootNode.get("model").asText();
        return rootNode.at("/choices/0/message/content").asText();
    }

}
