package be.intecbrussel.linguacards.dto;

public class DeckStatsResponse {

    private Long deckId;
    private long totalCards;
    private long newCards;
    private long dueCards;
    private long scheduledCards;

    public DeckStatsResponse(Long deckId, long totalCards, long newCards, long dueCards, long scheduledCards) {
        this.deckId = deckId;
        this.totalCards = totalCards;
        this.newCards = newCards;
        this.dueCards = dueCards;
        this.scheduledCards = scheduledCards;
    }

    public Long getDeckId() {
        return deckId;
    }

    public long getTotalCards() {
        return totalCards;
    }

    public long getNewCards() {
        return newCards;
    }

    public long getDueCards() {
        return dueCards;
    }

    public long getScheduledCards() {
        return scheduledCards;
    }
}