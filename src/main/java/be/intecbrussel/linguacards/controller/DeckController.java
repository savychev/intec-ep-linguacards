package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.api.dto.DeckResponse;
import be.intecbrussel.linguacards.dto.DeckRequest;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.security.CurrentUserService;
import be.intecbrussel.linguacards.service.DeckService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final CurrentUserService currentUserService;

    public DeckController(DeckService deckService, CurrentUserService currentUserService) {
        this.deckService = deckService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<DeckResponse> getUserDecks(@RequestParam(required = false) Long ownerId) {
        return deckService.getUserDecks(resolveOwnerId(ownerId)).stream()
                .map(DeckController::toResponse)
                .toList();
    }

    @GetMapping("/me")
    public List<DeckResponse> getCurrentUserDecks() {
        return deckService.getUserDecks(currentUserService.getCurrentUserId()).stream()
                .map(DeckController::toResponse)
                .toList();
    }

    @PostMapping
    public DeckResponse createDeck(@RequestParam(required = false) Long ownerId, @Valid @RequestBody DeckRequest request) {
        boolean isPrivate = request.getIsPrivate() != null ? request.getIsPrivate() : true;
        Deck deck = deckService.createDeck(resolveOwnerId(ownerId), request.getName(), request.getLanguageCode(), isPrivate);
        return toResponse(deck);
    }

    @PostMapping("/me")
    public DeckResponse createDeckForCurrentUser(@Valid @RequestBody DeckRequest request) {
        boolean isPrivate = request.getIsPrivate() != null ? request.getIsPrivate() : true;
        Deck deck = deckService.createDeck(
                currentUserService.getCurrentUserId(),
                request.getName(),
                request.getLanguageCode(),
                isPrivate
        );
        return toResponse(deck);
    }

    @GetMapping("/{deckId}")
    public DeckResponse getDeck(@RequestParam(required = false) Long ownerId, @PathVariable Long deckId) {
        return toResponse(deckService.getDeck(resolveOwnerId(ownerId), deckId));
    }

    @PutMapping("/{deckId}")
    public DeckResponse updateDeck(@RequestParam(required = false) Long ownerId, @PathVariable Long deckId, @Valid @RequestBody DeckRequest request) {
        Deck deck = deckService.updateDeck(resolveOwnerId(ownerId), deckId, request.getName(), request.getLanguageCode(), Boolean.TRUE.equals(request.getIsPrivate()));
        return toResponse(deck);
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@RequestParam(required = false) Long ownerId, @PathVariable Long deckId) {
        deckService.deleteDeck(resolveOwnerId(ownerId), deckId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveOwnerId(Long ownerId) {
        Long currentUserId;
        try {
            currentUserId = currentUserService.getCurrentUserId();
        } catch (IllegalArgumentException ex) {
            if (ownerId != null) {
                return ownerId;
            }
            throw ex;
        }

        if (ownerId != null && !ownerId.equals(currentUserId)) {
            throw new AccessDeniedException("ownerId does not match authenticated user");
        }

        return currentUserId;
    }

    private static DeckResponse toResponse(Deck deck) {
        return new DeckResponse(deck.getId(), deck.getName(), deck.getLanguageCode(), deck.isPrivate());
    }
}
