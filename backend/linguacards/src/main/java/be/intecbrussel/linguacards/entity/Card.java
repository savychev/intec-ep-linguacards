package be.intecbrussel.linguacards.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cards_deck_term", columnNames = {"deck_id", "term"})
}, indexes = {
        @Index(name = "idx_cards_deck_id", columnList = "deck_id")
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

    public List<ReviewLog> getReviewLogs() {
        return reviewLogs;
    }
}