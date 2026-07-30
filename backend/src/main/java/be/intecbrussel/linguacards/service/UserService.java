package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.exception.EmailAlreadyExistsException;
import be.intecbrussel.linguacards.exception.InvalidCredentialsException;
import be.intecbrussel.linguacards.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        String hash = passwordEncoder.encode(rawPassword);
        User user = new User(normalizedEmail, hash);

        return userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}