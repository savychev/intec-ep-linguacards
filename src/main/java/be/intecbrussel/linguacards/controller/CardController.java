package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.api.dto.CardResponse;
import be.intecbrussel.linguacards.dto.CardRequest;
import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.security.CurrentUserService;
import be.intecbrussel.linguacards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class CardController {

    private final CardService cardService;
    private final CurrentUserService currentUserService;

    public CardController(CardService cardService, CurrentUserService currentUserService) {
        this.cardService = cardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping({"/{deckId}/cards", "/me/{deckId}/cards"})
    public List<CardResponse> getCards(@PathVariable Long deckId) {
        return cardService.getCards(currentUserService.getCurrentUserId(), deckId).stream()
                .map(CardController::toResponse)
                .toList();
    }

    @PostMapping({"/{deckId}/cards", "/me/{deckId}/cards"})
    public CardResponse createCard(@PathVariable Long deckId, @Valid @RequestBody CardRequest request) {
        Card card = cardService.createCard(
                currentUserService.getCurrentUserId(),
                deckId,
                request.getTerm(),
                request.getDefinition(),
                request.getExample(),
                request.getCefrLevel(),
                request.getTags()
        );
        return toResponse(card);
    }

    @PutMapping({"/{deckId}/cards/{cardId}", "/me/{deckId}/cards/{cardId}"})
    public CardResponse updateCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody CardRequest request
    ) {
        Card card = cardService.updateCard(
                currentUserService.getCurrentUserId(),
                deckId,
                cardId,
                request.getTerm(),
                request.getDefinition(),
                request.getExample(),
                request.getCefrLevel(),
                request.getTags()
        );
        return toResponse(card);
    }

    @DeleteMapping({"/{deckId}/cards/{cardId}", "/me/{deckId}/cards/{cardId}"})
    public ResponseEntity<Void> deleteCard(@PathVariable Long deckId, @PathVariable Long cardId) {
        cardService.deleteCard(currentUserService.getCurrentUserId(), deckId, cardId);
        return ResponseEntity.noContent().build();
    }

    private static CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getTerm(),
                card.getDefinition(),
                card.getExample(),
                card.getCefrLevel(),
                card.getTags()
        );
    }

}
