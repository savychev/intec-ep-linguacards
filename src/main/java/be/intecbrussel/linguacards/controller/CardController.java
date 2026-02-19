package be.intecbrussel.linguacards.controller;

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
    public List<Card> getCards(@RequestParam Long ownerId, @PathVariable Long deckId) {
        return cardService.getCards(ownerId, deckId);
    }

    @PostMapping
    public Card createCard(@RequestParam Long ownerId, @PathVariable Long deckId, @Valid @RequestBody CardRequest request) {
        return cardService.createCard(
                ownerId,
                deckId,
                request.getTerm(),
                request.getDefinition(),
                request.getExample(),
                request.getCefrLevel(),
                request.getTags()
        );
    }

    @PutMapping("/{cardId}")
    public Card updateCard(
            @RequestParam Long ownerId,
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody CardRequest request
    ) {
        return cardService.updateCard(
                ownerId,
                deckId,
                cardId,
                request.getTerm(),
                request.getDefinition(),
                request.getExample(),
                request.getCefrLevel(),
                request.getTags()
        );
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@RequestParam Long ownerId, @PathVariable Long deckId, @PathVariable Long cardId) {
        cardService.deleteCard(ownerId, deckId, cardId);
        return ResponseEntity.noContent().build();
    }
}
