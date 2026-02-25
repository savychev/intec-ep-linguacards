package be.intecbrussel.linguacards.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cards_deck_term", columnNames = {"deck_id", "term"})
}, indexes = {
        @Index(name = "idx_cards_deck_id", columnList = "deck_id"),
        @Index(name = "idx_cards_next_review_at", columnList = "next_review_at")
})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cards_deck"))
    private Deck deck;

    @Column(nullable = false, length = 200)
    private String term;

    @Column(nullable = false, length = 2000)
    private String definition;

    @Column(length = 500)
    private String example;

    @Column(length = 5)
    private String cefr;

    @Column(length = 200)
    private String tags;

    // --- Training fields (MVP) ---
    @Column(name = "next_review_at")
    private Instant nextReviewAt;

    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays = 0;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLog> reviewLogs = new ArrayList<>();

    protected Card() {
    }

    public Card(Deck deck, String term, String definition) {
        this.deck = deck;
        this.term = term;
        this.definition = definition;
    }

    public Long getId() {
        return id;
    }

    public Deck getDeck() {
        return deck;
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

    public String getCefr() {
        return cefr;
    }

    public String getTags() {
        return tags;
    }

    public Instant getNextReviewAt() {
        return nextReviewAt;
    }

    public Instant getLastReviewedAt() {
        return lastReviewedAt;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    public List<ReviewLog> getReviewLogs() {
        return reviewLogs;
    }

    // setters for update / training
    public void setTerm(String term) {
        this.term = term;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setCefr(String cefr) {
        this.cefr = cefr;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setNextReviewAt(Instant nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public void setLastReviewedAt(Instant lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }

    public void setIntervalDays(int intervalDays) {
        this.intervalDays = intervalDays;
    }
}