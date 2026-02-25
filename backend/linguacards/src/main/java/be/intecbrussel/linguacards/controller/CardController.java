package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.CardCreateRequest;
import be.intecbrussel.linguacards.dto.CardResponse;
import be.intecbrussel.linguacards.dto.CardUpdateRequest;
import be.intecbrussel.linguacards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // List + create inside deck
    @GetMapping("/api/decks/{deckId}/cards")
    public List<CardResponse> list(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long deckId) {
        Long userId = jwt.getClaim("userId");
        return cardService.listCards(userId, deckId);
    }

    @PostMapping("/api/decks/{deckId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse create(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long deckId,
                               @Valid @RequestBody CardCreateRequest req) {
        Long userId = jwt.getClaim("userId");
        return cardService.createCard(userId, deckId, req);
    }

    // get/update/delete by cardId
    @GetMapping("/api/cards/{cardId}")
    public CardResponse getOne(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long cardId) {
        Long userId = jwt.getClaim("userId");
        return cardService.getCard(userId, cardId);
    }

    @PutMapping("/api/cards/{cardId}")
    public CardResponse update(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long cardId,
                               @Valid @RequestBody CardUpdateRequest req) {
        Long userId = jwt.getClaim("userId");
        return cardService.updateCard(userId, cardId, req);
    }

    @DeleteMapping("/api/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable Long cardId) {
        Long userId = jwt.getClaim("userId");
        cardService.deleteCard(userId, cardId);
    }
}