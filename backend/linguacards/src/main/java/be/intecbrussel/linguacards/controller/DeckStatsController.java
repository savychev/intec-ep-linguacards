package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.dto.DeckStatsResponse;
import be.intecbrussel.linguacards.service.DeckStatsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks/{deckId}/stats")
public class DeckStatsController {

    private final DeckStatsService deckStatsService;

    public DeckStatsController(DeckStatsService deckStatsService) {
        this.deckStatsService = deckStatsService;
    }

    @GetMapping
    public DeckStatsResponse stats(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long deckId) {
        Long userId = jwt.getClaim("userId");
        return deckStatsService.getMyDeckStats(userId, deckId);
    }
}