package be.intecbrussel.linguacards;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(properties = "app.security.require-auth-for-api=true")
class ApiSmokeIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void duplicateTermInSameDeckShouldFail() throws Exception {
        String ownerEmail = createOwnerEmail();
        Long deckId = createDeck(ownerEmail, "Deck1");

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(ownerEmail)))
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
                .andExpect(jsonPath("$.term").value("hello"));

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(ownerEmail)))
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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.message", containsString("Duplicate term")));
    }

    @Test
    void ownerIsolationShouldBlockAccessToOtherOwnerDeck() throws Exception {
        String ownerEmail = createOwnerEmail();
        String owner2Email = createOwnerEmail();
        Long deckId = createDeck(ownerEmail, "Deck1");

        mockMvc.perform(get("/api/decks/{deckId}", deckId)
                        .with(jwt().jwt(j -> j.subject(owner2Email))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("Deck not found")));

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(owner2Email)))
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("Deck not found")));
    }

    @Test
    void smokeFlowWithMockMvc() throws Exception {
        String ownerEmail = createOwnerEmail();
        Long deckId = createDeck(ownerEmail, "Deck1");

        MvcResult createCardResult = mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(ownerEmail)))
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
                        .with(jwt().jwt(j -> j.subject(ownerEmail)))
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/decks/{deckId}/cards/{cardId}/review", deckId, cardId)
                        .with(jwt().jwt(j -> j.subject(ownerEmail)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": "GOOD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId));

        mockMvc.perform(get("/api/decks/{deckId}/stats", deckId)
                        .with(jwt().jwt(j -> j.subject(ownerEmail))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCards").value(1))
                .andExpect(jsonPath("$.totalReviews", greaterThanOrEqualTo(1)));
    }

    private String createOwnerEmail() {
        String email = "test+" + UUID.randomUUID() + "@example.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash("hash")
                .build());
        return email;
    }

    private Long createDeck(String ownerEmail, String name) throws Exception {
        MvcResult createDeckResult = mockMvc.perform(post("/api/decks")
                        .with(jwt().jwt(j -> j.subject(ownerEmail)))
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
