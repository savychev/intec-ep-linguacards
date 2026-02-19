package be.intecbrussel.linguacards.api.auth;

public record AuthResponse(
        String token,
        Long userId,
        String email
) {
}
