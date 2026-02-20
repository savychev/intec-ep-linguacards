package be.intecbrussel.linguacards.security;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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
    void getCurrentUserIdShouldResolveUserFromAuthenticationName() {
        setAuthentication("owner@example.com");
        Mockito.when(userRepository.findByEmail("owner@example.com"))
                .thenReturn(Optional.of(User.builder().id(42L).email("owner@example.com").build()));

        Long currentUserId = currentUserService.getCurrentUserId();

        assertEquals(42L, currentUserId);
    }

    @Test
    void getCurrentUserIdShouldThrowWhenUserCannotBeFound() {
        setAuthentication("missing@example.com");
        Mockito.when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, currentUserService::getCurrentUserId);

        assertEquals("User not found", exception.getMessage());
    }

    private void setAuthentication(String email) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(email, "password"));
    }
}
