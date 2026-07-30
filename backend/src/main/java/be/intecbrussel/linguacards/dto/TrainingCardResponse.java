package be.intecbrussel.linguacards.dto;

import java.time.Instant;

public class TrainingCardResponse {

    private Long cardId;
    private Long deckId;
    private String term;
    private String definition;
    private String example;

    private Instant lastReviewedAt;
    private Instant nextReviewAt;
    private int intervalDays;

    public TrainingCardResponse(Long cardId,
                                Long deckId,
                                String term,
                                String definition,
                                String example,
                                Instant lastReviewedAt,
                                Instant nextReviewAt,
                                int intervalDays) {
        this.cardId = cardId;
        this.deckId = deckId;
        this.term = term;
        this.definition = definition;
        this.example = example;
        this.lastReviewedAt = lastReviewedAt;
        this.nextReviewAt = nextReviewAt;
        this.intervalDays = intervalDays;
    }

    public Long getCardId() {
        return cardId;
    }

    public Long getDeckId() {
        return deckId;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public String getExample() {
        return example;
    }

    public Instant getLastReviewedAt() {
        return lastReviewedAt;
    }

    public Instant getNextReviewAt() {
        return nextReviewAt;
    }

    public int getIntervalDays() {
        return intervalDays;
    }
}