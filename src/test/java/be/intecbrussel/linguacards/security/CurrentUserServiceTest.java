package be.intecbrussel.linguacards.security;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CurrentUserService currentUserService = new CurrentUserService(userRepository);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserIdShouldResolveUserFromJwtSubject() {
        setJwtAuthentication("owner@example.com");
        Mockito.when(userRepository.findByEmail("owner@example.com"))
                .thenReturn(Optional.of(User.builder().id(42L).email("owner@example.com").build()));

        Long currentUserId = currentUserService.getCurrentUserId();

        assertEquals(42L, currentUserId);
    }

    @Test
    void getCurrentUserIdShouldThrowWhenUserCannotBeFound() {
        setJwtAuthentication("missing@example.com");
        Mockito.when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, currentUserService::getCurrentUserId);

        assertEquals("User not found", exception.getMessage());
    }

    private void setJwtAuthentication(String subject) {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                java.util.Map.of("alg", "HS256"),
                java.util.Map.of("sub", subject)
        );
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }
}
