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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(properties = "app.security.require-auth-for-api=true")
class DeckMeEndpointsAuthIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void decksMeEndpointsShouldUseCurrentUserFromJwt() throws Exception {
        String email = "jwt-owner@example.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash("hash")
                .build());

        mockMvc.perform(post("/api/decks/me")
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "JWT Deck",
                                  "languageCode": "en",
                                  "isPrivate": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("JWT Deck"))
                .andExpect(jsonPath("$.languageCode").value("en"))
                .andExpect(jsonPath("$.isPrivate").value(true));

        mockMvc.perform(get("/api/decks/me")
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("JWT Deck"))
                .andExpect(jsonPath("$[0].languageCode").value("en"))
                .andExpect(jsonPath("$[0].isPrivate").value(true));
    }

    @Test
    void nestedMeEndpointsShouldNotNeedOwnerId() throws Exception {
        String email = "jwt-owner-2@example.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash("hash")
                .build());

        MvcResult createDeckResult = mockMvc.perform(post("/api/decks/me")
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Deck Me",
                                  "languageCode": "en"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPrivate").value(true))
                .andReturn();

        Long deckId = objectMapper.readTree(createDeckResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult createCardResult = mockMvc.perform(post("/api/decks/me/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "term": "hello",
                                  "definition": "a greeting",
                                  "cefrLevel": "A1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.term").value("hello"))
                .andReturn();

        JsonNode createdCard = objectMapper.readTree(createCardResult.getResponse().getContentAsString());
        Long cardId = createdCard.get("id").asLong();

        mockMvc.perform(get("/api/decks/me/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/decks/me/{deckId}/training", deckId)
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/api/decks/me/{deckId}/cards/{cardId}/review", deckId, cardId)
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": "GOOD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId));

        mockMvc.perform(get("/api/decks/me/{deckId}/stats", deckId)
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCards").value(1))
                .andExpect(jsonPath("$.totalReviews", greaterThanOrEqualTo(1)));
    }

    @Test
    void nonMeEndpointsShouldFallbackToCurrentUserWhenOwnerIdMissing() throws Exception {
        String email = "jwt-owner-3@example.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash("hash")
                .build());

        MvcResult createDeckResult = mockMvc.perform(post("/api/decks")
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Deck No OwnerId",
                                  "languageCode": "en"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Long deckId = objectMapper.readTree(createDeckResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult createCardResult = mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "term": "world",
                                  "definition": "earth",
                                  "cefrLevel": "A1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.term").value("world"))
                .andReturn();

        Long cardId = objectMapper.readTree(createCardResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/decks/{deckId}/cards", deckId)
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/decks/{deckId}/training", deckId)
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/api/decks/{deckId}/cards/{cardId}/review", deckId, cardId)
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": "GOOD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId));

        mockMvc.perform(get("/api/decks/{deckId}/stats", deckId)
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCards").value(1));
    }

    @Test
    void meAndCanonicalRoutesShouldShareSameBehavior() throws Exception {
        String email = "jwt-owner-6@example.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash("hash")
                .build());

        MvcResult createDeckResult = mockMvc.perform(post("/api/decks")
                        .with(jwt().jwt(j -> j.subject(email)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Unified Routes",
                                  "languageCode": "en"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Long deckId = objectMapper.readTree(createDeckResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/decks/me")
                        .with(jwt().jwt(j -> j.subject(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Unified Routes"));

        mockMvc.perform(get("/api/decks/{deckId}/training", deckId)
                        .with(jwt().jwt(j -> j.subject(email)))
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/decks/me/{deckId}/training", deckId)
                        .with(jwt().jwt(j -> j.subject(email)))
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void nonMeEndpointsShouldIgnoreOwnerIdForAuthenticatedUser() throws Exception {
        User owner = userRepository.save(User.builder()
                .email("jwt-owner-4@example.com")
                .passwordHash("hash")
                .build());
        User otherUser = userRepository.save(User.builder()
                .email("jwt-owner-5@example.com")
                .passwordHash("hash")
                .build());

        mockMvc.perform(get("/api/decks")
                        .with(jwt().jwt(j -> j.subject(owner.getEmail())))
                        .param("ownerId", String.valueOf(otherUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
