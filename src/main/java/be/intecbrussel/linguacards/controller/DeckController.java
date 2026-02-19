package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.DeckRequest;
import be.intecbrussel.linguacards.entity.Deck;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping
    public List<Deck> getUserDecks(@RequestParam Long ownerId) {
        return deckService.getUserDecks(ownerId);
    }

    @PostMapping
    public Deck createDeck(@RequestParam Long ownerId, @Valid @RequestBody DeckRequest request) {
        return deckService.createDeck(ownerId, request.getName(), request.getLanguageCode(), request.isPrivate());
    }

    @GetMapping("/{deckId}")
    public Deck getDeck(@RequestParam Long ownerId, @PathVariable Long deckId) {
        return deckService.getDeck(ownerId, deckId);
    }

    @PutMapping("/{deckId}")
    public Deck updateDeck(@RequestParam Long ownerId, @PathVariable Long deckId, @Valid @RequestBody DeckRequest request) {
        return deckService.updateDeck(ownerId, deckId, request.getName(), request.getLanguageCode(), request.isPrivate());
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@RequestParam Long ownerId, @PathVariable Long deckId) {
        deckService.deleteDeck(ownerId, deckId);
        return ResponseEntity.noContent().build();
    }
}
