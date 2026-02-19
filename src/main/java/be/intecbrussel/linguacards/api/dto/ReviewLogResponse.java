package be.intecbrussel.linguacards.api.dto;

import java.time.Instant;

public record ReviewLogResponse(Long id, String rating, Instant reviewedAt, Long cardId) {
}
