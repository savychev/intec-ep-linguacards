package be.intecbrussel.linguacards.api.auth;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.security.JwtService;
import be.intecbrussel.linguacards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.email(), request.password());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticate(request.email(), request.password());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}
