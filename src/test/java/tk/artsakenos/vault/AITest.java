package tk.artsakenos.vault;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tk.artsakenos.vault.service.AIService;

import java.io.IOException;

@Disabled
@SpringBootTest
@Slf4j
public class AITest {

    @Autowired
    private AIService aiService;

    @Test
    void testAi() throws IOException {
        String keywords = aiService.retrieveKeywords("how is it fixed the price in an auction?");
        System.out.println(keywords);
    }

}
