package be.intecbrussel.linguacards.dto.auth;

public class AuthResponse {

    private Long id;
    private String email;

    public AuthResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}