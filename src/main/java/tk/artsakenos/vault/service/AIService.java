package tk.artsakenos.vault.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperHttpClient;
import tk.artsakenos.vault.libraries.Helper;

import java.util.Map;

@SuppressWarnings("unused")
@Service
@Slf4j
public class AIService {

    private SuperHttpClient client;

    @Value("${vault.key_groq_inf}")
    private String KEY_GROQ;


    public static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    public static String GROQ_SYSTEM_QUERY_KEYWORD = """
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
                        "content": "SYSTEM",
                        "role": "system"
                    },
                    {
                        "content": "CONTENT",
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
        GROQ_SYSTEM_QUERY_KEYWORD = Helper.getFromResources("/prompts/prompt_keword_extractor.txt");
    }

    public String retrieveKeywords(String query) {
        String jsonQuestion = Helper.jsonizeString(query);
        String jsonSystem = Helper.jsonizeString(GROQ_SYSTEM_QUERY_KEYWORD);
        String jsonBody = GROQ_JSON_WRAPPER
                .replaceAll("SYSTEM", jsonSystem)
                .replaceAll("CONTENT", jsonQuestion);

        Map<String, String> postParameters = Map.of(
                "Authorization", "Bearer " + KEY_GROQ);
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
