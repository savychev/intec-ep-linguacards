package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.DeckCreateRequest;
import be.intecbrussel.linguacards.dto.DeckResponse;
import be.intecbrussel.linguacards.dto.DeckUpdateRequest;
import be.intecbrussel.linguacards.service.DeckService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping
    public List<DeckResponse> listMyDecks(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return deckService.listMyDecks(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeckResponse create(@AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody DeckCreateRequest req) {
        Long userId = jwt.getClaim("userId");
        return deckService.createDeck(userId, req);
    }

    @GetMapping("/{id}")
    public DeckResponse getOne(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id) {
        Long userId = jwt.getClaim("userId");
        return deckService.getMyDeck(userId, id);
    }

    @PutMapping("/{id}")
    public DeckResponse update(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id,
                               @Valid @RequestBody DeckUpdateRequest req) {
        Long userId = jwt.getClaim("userId");
        return deckService.updateMyDeck(userId, id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable Long id) {
        Long userId = jwt.getClaim("userId");
        deckService.deleteMyDeck(userId, id);
    }
}