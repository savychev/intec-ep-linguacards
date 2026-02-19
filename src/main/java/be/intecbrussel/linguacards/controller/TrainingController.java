package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.ReviewRequest;
import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.entity.ReviewLog;
import be.intecbrussel.linguacards.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/{deckId}/training")
    public List<Card> getTrainingCards(
            @RequestParam Long ownerId,
            @PathVariable Long deckId,
            @RequestParam(defaultValue = "0") int limit
    ) {
        return trainingService.getTrainingCards(ownerId, deckId, limit);
    }

    @PostMapping("/{deckId}/cards/{cardId}/review")
    public ReviewLog logReview(
            @RequestParam Long ownerId,
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return trainingService.logReview(ownerId, deckId, cardId, request.getRating());
    }
}
