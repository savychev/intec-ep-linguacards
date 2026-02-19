package be.intecbrussel.linguacards;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ApiSmokeIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void smokeFlowWithMockMvc() throws Exception {
        User owner = userRepository.save(User.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .build());
        Long ownerId = owner.getId();

        MvcResult createDeckResult = mockMvc.perform(post("/api/decks")
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Deck1",
                                  "languageCode": "en",
                                  "isPrivate": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        JsonNode deckJson = objectMapper.readTree(createDeckResult.getResponse().getContentAsString());
        Long deckId = deckJson.get("id").asLong();

        MvcResult createCardResult = mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "term": "hello",
                                  "definition": "a greeting",
                                  "example": null,
                                  "cefrLevel": "A1",
                                  "tags": "greeting"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        JsonNode cardJson = objectMapper.readTree(createCardResult.getResponse().getContentAsString());
        Long cardId = cardJson.get("id").asLong();

        mockMvc.perform(get("/api/decks/{deckId}/training", deckId)
                        .param("ownerId", String.valueOf(ownerId))
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/decks/{deckId}/cards/{cardId}/review", deckId, cardId)
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": "GOOD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value("GOOD"));

        mockMvc.perform(get("/api/decks/{deckId}/stats", deckId)
                        .param("ownerId", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCards").value(1))
                .andExpect(jsonPath("$.totalReviews", greaterThanOrEqualTo(1)));
    }
}
