package be.intecbrussel.linguacards.api.auth;

public record AuthResponse(
        Long userId,
        String email
) {
}
