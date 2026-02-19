package be.intecbrussel.linguacards.controller;

import be.intecbrussel.linguacards.service.StatsService;
import be.intecbrussel.linguacards.service.model.DeckStats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decks")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/{deckId}/stats")
    public DeckStats getDeckStats(@RequestParam Long ownerId, @PathVariable Long deckId) {
        return statsService.getDeckStats(ownerId, deckId);
    }
}
