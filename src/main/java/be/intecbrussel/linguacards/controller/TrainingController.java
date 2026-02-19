package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.api.dto.CardResponse;
import be.intecbrussel.linguacards.api.dto.ReviewLogResponse;
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
    public List<CardResponse> getTrainingCards(
            @RequestParam Long ownerId,
            @PathVariable Long deckId,
            @RequestParam(defaultValue = "0") int limit
    ) {
        return trainingService.getTrainingCards(ownerId, deckId, limit).stream()
                .map(TrainingController::toCardResponse)
                .toList();
    }

    @PostMapping("/{deckId}/cards/{cardId}/review")
    public ReviewLogResponse logReview(
            @RequestParam Long ownerId,
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewLog reviewLog = trainingService.logReview(ownerId, deckId, cardId, request.getRating());
        return toReviewLogResponse(reviewLog);
    }

    private static CardResponse toCardResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getTerm(),
                card.getDefinition(),
                card.getExample(),
                card.getCefrLevel(),
                card.getTags()
        );
    }

    private static ReviewLogResponse toReviewLogResponse(ReviewLog reviewLog) {
        return new ReviewLogResponse(
                reviewLog.getId(),
                reviewLog.getRating().name(),
                reviewLog.getReviewedAt(),
                reviewLog.getCard().getId()
        );
    }
}
