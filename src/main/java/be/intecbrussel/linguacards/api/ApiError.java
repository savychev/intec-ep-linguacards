package be.intecbrussel.linguacards.api;

import java.time.Instant;

public record ApiError(
        String code,
        String message,
        Object details,
        String path,
        Instant timestamp
) {
}
