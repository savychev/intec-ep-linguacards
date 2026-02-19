package be.intecbrussel.linguacards.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeckStats {
    private final long totalCards;
    private final long totalReviews;
    private final long againCount;
    private final long hardCount;
    private final long goodCount;
    private final long easyCount;
}
