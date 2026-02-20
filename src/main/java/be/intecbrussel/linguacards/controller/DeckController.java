package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.api.dto.DeckResponse;
import be.intecbrussel.linguacards.dto.DeckRequest;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.security.CurrentUserService;
import be.intecbrussel.linguacards.service.DeckService;
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
public class DeckController {

    private final DeckService deckService;
    private final CurrentUserService currentUserService;

    public DeckController(DeckService deckService, CurrentUserService currentUserService) {
        this.deckService = deckService;
        this.currentUserService = currentUserService;
    }

    @GetMapping({"", "/me"})
    public List<DeckResponse> getCurrentUserDecks() {
        return deckService.getUserDecks(currentUserService.getCurrentUserId()).stream()
                .map(DeckController::toResponse)
                .toList();
    }

    @PostMapping({"", "/me"})
    public DeckResponse createDeck(@Valid @RequestBody DeckRequest request) {
        boolean isPrivate = request.getIsPrivate() == null || request.getIsPrivate();
        Deck deck = deckService.createDeck(currentUserService.getCurrentUserId(), request.getName(), request.getLanguageCode(), isPrivate);
        return toResponse(deck);
    }

    @GetMapping("/{deckId}")
    public DeckResponse getDeck(@PathVariable Long deckId) {
        return toResponse(deckService.getDeck(currentUserService.getCurrentUserId(), deckId));
    }

    @PutMapping("/{deckId}")
    public DeckResponse updateDeck(@PathVariable Long deckId, @Valid @RequestBody DeckRequest request) {
        Deck deck = deckService.updateDeck(currentUserService.getCurrentUserId(), deckId, request.getName(), request.getLanguageCode(), Boolean.TRUE.equals(request.getIsPrivate()));
        return toResponse(deck);
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long deckId) {
        deckService.deleteDeck(currentUserService.getCurrentUserId(), deckId);
        return ResponseEntity.noContent().build();
    }

    private static DeckResponse toResponse(Deck deck) {
        return new DeckResponse(deck.getId(), deck.getName(), deck.getLanguageCode(), deck.isPrivate());
    }
}
