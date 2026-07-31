package be.intecbrussel.linguacards;

import be.intecbrussel.linguacards.repository.ReviewLogRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CoreApiIntegrationTests {

    private static final String PASSWORD = "correct-horse-battery-staple";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewLogRepository reviewLogRepository;

    @Test
    void registrationLoginAndMeEndpointWorkTogether() throws Exception {
        register("MixedCase@Example.com");

        String token = login("mixedcase@example.com", PASSWORD);

        mockMvc.perform(get("/api/me")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mixedcase@example.com"))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.issuer").value("https://linguacards.test"));

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateEmailAndInvalidCredentialsReturnExpectedErrors() throws Exception {
        register("existing@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsJson("EXISTING@example.com", PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsJson("existing@example.com", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    void invalidRegistrationReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsJson("not-an-email", PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("email:")))
                .andExpect(jsonPath("$.path").value("/api/auth/register"));
    }

    @Test
    void userCanManageOwnDecksButCannotAccessAnotherUsersDeck() throws Exception {
        String ownerToken = registerAndLogin("deck-owner@example.com");
        String otherToken = registerAndLogin("deck-other@example.com");
        Long deckId = createDeck(ownerToken, "Dutch B2");

        mockMvc.perform(get("/api/decks")
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Dutch B2"));

        mockMvc.perform(put("/api/decks/{id}", deckId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deckJson("Dutch C1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deckId.intValue()))
                .andExpect(jsonPath("$.name").value("Dutch C1"));

        mockMvc.perform(get("/api/decks")
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/decks/{id}", deckId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/decks/{id}", deckId)
                        .header("Authorization", bearer(otherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deckJson("Stolen deck")))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/decks/{id}", deckId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/decks/{id}", deckId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/decks/{id}", deckId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void userCanManageOwnCardsButCannotAccessAnotherUsersCard() throws Exception {
        String ownerToken = registerAndLogin("card-owner@example.com");
        String otherToken = registerAndLogin("card-other@example.com");
        Long deckId = createDeck(ownerToken, "Portuguese");
        Long cardId = createCard(ownerToken, deckId, "saudade");

        mockMvc.perform(get("/api/decks/{deckId}/cards", deckId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].term").value("saudade"));

        mockMvc.perform(put("/api/cards/{cardId}", cardId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson("saudade profunda")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.intValue()))
                .andExpect(jsonPath("$.term").value("saudade profunda"));

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson("SAUDADE PROFUNDA")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mockMvc.perform(get("/api/decks/{deckId}/cards", deckId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .header("Authorization", bearer(otherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson("forbidden")))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/cards/{cardId}", cardId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/cards/{cardId}", cardId)
                        .header("Authorization", bearer(otherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson("stolen")))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/cards/{cardId}", cardId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/cards/{cardId}", cardId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/decks/{deckId}/cards", deckId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void trainingFlowUpdatesScheduleAndStatistics() throws Exception {
        String token = registerAndLogin("training@example.com");
        Long deckId = createDeck(token, "French");

        mockMvc.perform(post("/api/decks/{deckId}/train/start", deckId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasCard").value(false))
                .andExpect(jsonPath("$.reason").value("EMPTY_DECK"))
                .andExpect(jsonPath("$.card").doesNotExist());

        Long cardId = createCard(token, deckId, "pourtant");

        mockMvc.perform(get("/api/decks/{deckId}/stats", deckId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCards").value(1))
                .andExpect(jsonPath("$.newCards").value(1))
                .andExpect(jsonPath("$.dueCards").value(0))
                .andExpect(jsonPath("$.scheduledCards").value(0));

        mockMvc.perform(post("/api/decks/{deckId}/train/start", deckId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasCard").value(true))
                .andExpect(jsonPath("$.reason").doesNotExist())
                .andExpect(jsonPath("$.card.cardId").value(cardId.intValue()))
                .andExpect(jsonPath("$.card.intervalDays").value(0));

        mockMvc.perform(post("/api/cards/{cardId}/review", cardId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson("GOOD")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(cardId.intValue()))
                .andExpect(jsonPath("$.intervalDays").value(7))
                .andExpect(jsonPath("$.lastReviewedAt").isNotEmpty())
                .andExpect(jsonPath("$.nextReviewAt").isNotEmpty());

        assertThat(reviewLogRepository.count()).isEqualTo(1);

        mockMvc.perform(get("/api/decks/{deckId}/stats", deckId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCards").value(1))
                .andExpect(jsonPath("$.newCards").value(0))
                .andExpect(jsonPath("$.dueCards").value(0))
                .andExpect(jsonPath("$.scheduledCards").value(1));

        mockMvc.perform(post("/api/decks/{deckId}/train/next", deckId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasCard").value(false))
                .andExpect(jsonPath("$.reason").value("NO_CARDS_TO_TRAIN"))
                .andExpect(jsonPath("$.card").doesNotExist());
    }

    @ParameterizedTest
    @CsvSource({
            "AGAIN, 1",
            "HARD, 3",
            "GOOD, 7",
            "EASY, 14"
    })
    void reviewRatingSetsExpectedInterval(String rating, int expectedDays) throws Exception {
        String token = registerAndLogin("rating-" + rating.toLowerCase() + "@example.com");
        Long deckId = createDeck(token, "Rating deck");
        Long cardId = createCard(token, deckId, "rating " + rating.toLowerCase());

        mockMvc.perform(post("/api/cards/{cardId}/review", cardId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson(rating)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intervalDays").value(expectedDays));
    }

    @Test
    void trainingAndStatisticsAreOwnerIsolated() throws Exception {
        String ownerToken = registerAndLogin("training-owner@example.com");
        String otherToken = registerAndLogin("training-other@example.com");
        Long deckId = createDeck(ownerToken, "Private training");
        Long cardId = createCard(ownerToken, deckId, "private card");

        mockMvc.perform(post("/api/decks/{deckId}/train/start", deckId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/cards/{cardId}/review", cardId)
                        .header("Authorization", bearer(otherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson("GOOD")))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/decks/{deckId}/stats", deckId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/decks/{deckId}/train/start", deckId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.cardId").value(cardId.intValue()));
    }

    private String registerAndLogin(String email) throws Exception {
        register(email);
        return login(email, PASSWORD);
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsJson(email, PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("registered"));
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsJson(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private Long createDeck(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/decks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deckJson(name)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private Long createCard(String token, Long deckId, String term) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/decks/{deckId}/cards", deckId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJson(term)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.term").value(term))
                .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private String credentialsJson(String email, String password) {
        return """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);
    }

    private String deckJson(String name) {
        return """
                {"name":"%s","languageCode":"nl","isPrivate":true}
                """.formatted(name);
    }

    private String cardJson(String term) {
        return """
                {"term":"%s","definition":"A clear monolingual definition"}
                """.formatted(term);
    }

    private String reviewJson(String rating) {
        return """
                {"rating":"%s"}
                """.formatted(rating);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
