package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.TrainingCardResponse;
import be.intecbrussel.linguacards.service.TrainingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks/{deckId}/train")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/next")
    public TrainingCardResponse next(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable Long deckId) {
        Long userId = jwt.getClaim("userId");
        return trainingService.nextCard(userId, deckId);
    }
}