package be.intecbrussel.linguacards.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "decks", indexes = {
        @Index(name = "idx_decks_owner_id", columnList = "owner_id")
})
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_decks_owner"))
    private User owner;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 10, name = "language_code")
    private String languageCode;

    @Column(nullable = false)
    private boolean isPrivate = true;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    protected Deck() {
    }

    public Deck(User owner, String name, String languageCode, boolean isPrivate) {
        this.owner = owner;
        this.name = name;
        this.languageCode = languageCode;
        this.isPrivate = isPrivate;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public List<Card> getCards() {
        return cards;
    }
}