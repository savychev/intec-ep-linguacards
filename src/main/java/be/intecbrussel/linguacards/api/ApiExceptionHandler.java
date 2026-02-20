package be.intecbrussel.linguacards.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String message = ex.getMessage() == null ? "Invalid request" : ex.getMessage();

        if ("Invalid credentials".equals(message) || "No authenticated user".equals(message)) {
            return buildError(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", message, null, request);
        }

        if (message.endsWith("not found") || "User not found".equals(message)) {
            return buildError(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message, null, request);
        }

        if (message.startsWith("Duplicate") || "Email already exists".equals(message)) {
            return buildError(HttpStatus.CONFLICT, "RESOURCE_CONFLICT", message, null, request);
        }

        return buildError(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION", message, null, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "AUTH_FORBIDDEN", safeMessage(ex.getMessage(), "Forbidden"), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "VALIDATION_FAILED",
                "Validation failed",
                details,
                request
        );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String code,
            String message,
            Object details,
            HttpServletRequest request
    ) {
        ApiError body = new ApiError(
                code,
                safeMessage(message, status.getReasonPhrase()),
                details,
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(status).body(body);
    }

    private String safeMessage(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }
}
