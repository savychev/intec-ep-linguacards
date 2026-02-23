package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.auth.AuthResponse;
import be.intecbrussel.linguacards.dto.auth.RegisterRequest;
import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword());
        return new AuthResponse(user.getId(), user.getEmail());
    }
}