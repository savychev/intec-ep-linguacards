package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.security.CurrentUserService;
import be.intecbrussel.linguacards.service.StatsService;
import be.intecbrussel.linguacards.service.model.DeckStats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decks")
public class StatsController {

    private final StatsService statsService;
    private final CurrentUserService currentUserService;

    public StatsController(StatsService statsService, CurrentUserService currentUserService) {
        this.statsService = statsService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{deckId}/stats")
    public DeckStats getDeckStats(@PathVariable Long deckId) {
        return statsService.getDeckStats(currentUserService.getCurrentUserId(), deckId);
    }

    @GetMapping("/me/{deckId}/stats")
    public DeckStats getCurrentUserDeckStats(@PathVariable Long deckId) {
        return statsService.getDeckStats(currentUserService.getCurrentUserId(), deckId);
    }

}
