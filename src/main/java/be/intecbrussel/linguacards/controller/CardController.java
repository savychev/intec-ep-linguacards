package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.api.dto.CardResponse;
import be.intecbrussel.linguacards.dto.CardRequest;
import be.intecbrussel.linguacards.entity.Card;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decks/{deckId}/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponse> getCards(@RequestParam Long ownerId, @PathVariable Long deckId) {
        return cardService.getCards(ownerId, deckId).stream()
                .map(CardController::toResponse)
                .toList();
    }

    @PostMapping
    public CardResponse createCard(@RequestParam Long ownerId, @PathVariable Long deckId, @Valid @RequestBody CardRequest request) {
        Card card = cardService.createCard(
                ownerId,
                deckId,
                request.getTerm(),
                request.getDefinition(),
                request.getExample(),
                request.getCefrLevel(),
                request.getTags()
        );
        return toResponse(card);
    }

    @PutMapping("/{cardId}")
    public CardResponse updateCard(
            @RequestParam Long ownerId,
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody CardRequest request
    ) {
        Card card = cardService.updateCard(
                ownerId,
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

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@RequestParam Long ownerId, @PathVariable Long deckId, @PathVariable Long cardId) {
        cardService.deleteCard(ownerId, deckId, cardId);
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
