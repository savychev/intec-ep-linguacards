package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.ReviewRequest;
import be.intecbrussel.linguacards.dto.TrainingCardResponse;
import be.intecbrussel.linguacards.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/api/decks/{deckId}/train/next")
    public TrainingCardResponse next(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable Long deckId) {
        Long userId = jwt.getClaim("userId");
        return trainingService.nextCard(userId, deckId);
    }

    @PostMapping("/api/cards/{cardId}/review")
    public TrainingCardResponse review(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable Long cardId,
                                       @Valid @RequestBody ReviewRequest req) {
        Long userId = jwt.getClaim("userId");
        return trainingService.reviewCard(userId, cardId, req.getRating());
    }
}