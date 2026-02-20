package be.intecbrussel.linguacards.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email @NotBlank @Size(max = 320) String email,
        @NotBlank @Size(min = 4, max = 255) String password
) {
}
