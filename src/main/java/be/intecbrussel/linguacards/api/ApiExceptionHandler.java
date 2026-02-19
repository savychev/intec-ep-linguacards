package be.intecbrussel.linguacards.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        if ("Invalid credentials".equals(ex.getMessage())) {
            return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
        }
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
