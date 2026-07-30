package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.AuthResponse;
import be.intecbrussel.linguacards.dto.LoginRequest;
import be.intecbrussel.linguacards.dto.RegisterRequest;
import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.security.JwtTokenService;
import be.intecbrussel.linguacards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public AuthController(UserService userService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request.getEmail(), request.getPassword());
        return new AuthResponse("registered");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtTokenService.generateToken(user);
        return new AuthResponse("ok", token, "Bearer");
    }
}