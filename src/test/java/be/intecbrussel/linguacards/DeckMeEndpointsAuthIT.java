package be.intecbrussel.linguacards;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.repository.UserRepository;
import be.intecbrussel.linguacards.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
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
    private JwtService jwtService;

    @Test
    void decksMeEndpointsShouldUseCurrentUserFromJwt() throws Exception {
        User user = userRepository.save(User.builder()
                .email("jwt-owner@example.com")
                .passwordHash("hash")
                .build());
        String token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/decks/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "JWT Deck",
                                  "languageCode": "en",
                                  "isPrivate": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.id").value(user.getId()));

        mockMvc.perform(get("/api/decks/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].owner.id").value(user.getId()));
    }
}
