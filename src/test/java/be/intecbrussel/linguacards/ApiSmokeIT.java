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
import static org.hamcrest.Matchers.containsString;
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
    void duplicateTermInSameDeckShouldFail() throws Exception {
        Long ownerId = createOwner("test@example.com");
        Long deckId = createDeck(ownerId, "Deck1");

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
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
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "term": "hello",
                                  "definition": "another meaning",
                                  "example": null,
                                  "cefrLevel": "A1",
                                  "tags": "duplicate"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Duplicate term")));
    }

    @Test
    void ownerIsolationShouldBlockAccessToOtherOwnerDeck() throws Exception {
        Long ownerId = createOwner("test@example.com");
        Long owner2Id = createOwner("other@example.com");
        Long deckId = createDeck(ownerId, "Deck1");

        mockMvc.perform(get("/api/decks/{deckId}", deckId)
                        .param("ownerId", String.valueOf(owner2Id)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Deck not found")));

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .param("ownerId", String.valueOf(owner2Id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "term": "intrusion",
                                  "definition": "should fail",
                                  "example": null,
                                  "cefrLevel": "A1",
                                  "tags": "security"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Deck not found")));
    }

    @Test
    void smokeFlowWithMockMvc() throws Exception {
        Long ownerId = createOwner("test@example.com");

        Long deckId = createDeck(ownerId, "Deck1");

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

    private Long createOwner(String email) {
        User owner = userRepository.save(User.builder()
                .email(email)
                .passwordHash("hash")
                .build());
        return owner.getId();
    }

    private Long createDeck(Long ownerId, String name) throws Exception {
        MvcResult createDeckResult = mockMvc.perform(post("/api/decks")
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "languageCode": "en",
                                  "isPrivate": true
                                }
                                """.formatted(name)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        JsonNode deckJson = objectMapper.readTree(createDeckResult.getResponse().getContentAsString());
        return deckJson.get("id").asLong();
    }
}
