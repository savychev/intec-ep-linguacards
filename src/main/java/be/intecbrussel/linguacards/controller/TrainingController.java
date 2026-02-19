package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.api.dto.CardResponse;
import be.intecbrussel.linguacards.api.dto.ReviewLogResponse;
import be.intecbrussel.linguacards.dto.ReviewRequest;
import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.entity.ReviewLog;
import be.intecbrussel.linguacards.security.CurrentUserService;
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
    private final CurrentUserService currentUserService;

    public TrainingController(TrainingService trainingService, CurrentUserService currentUserService) {
        this.trainingService = trainingService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{deckId}/training")
    public List<CardResponse> getTrainingCards(
            @RequestParam(required = false) Long ownerId,
            @PathVariable Long deckId,
            @RequestParam(defaultValue = "0") int limit
    ) {
        return trainingService.getTrainingCards(resolveOwnerId(ownerId), deckId, limit).stream()
                .map(TrainingController::toCardResponse)
                .toList();
    }

    @PostMapping("/{deckId}/cards/{cardId}/review")
    public ReviewLogResponse logReview(
            @RequestParam(required = false) Long ownerId,
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewLog reviewLog = trainingService.logReview(resolveOwnerId(ownerId), deckId, cardId, request.getRating());
        return toReviewLogResponse(reviewLog);
    }

    @GetMapping("/me/{deckId}/training")
    public List<CardResponse> getCurrentUserTrainingCards(
            @PathVariable Long deckId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return trainingService.getTrainingCards(currentUserService.getCurrentUserId(), deckId, limit).stream()
                .map(TrainingController::toCardResponse)
                .toList();
    }

    @PostMapping("/me/{deckId}/cards/{cardId}/review")
    public ReviewLogResponse logReviewForCurrentUser(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewLog reviewLog = trainingService.logReview(currentUserService.getCurrentUserId(), deckId, cardId, request.getRating());
        return toReviewLogResponse(reviewLog);
    }

    private Long resolveOwnerId(Long ownerId) {
        return ownerId != null ? ownerId : currentUserService.getCurrentUserId();
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
