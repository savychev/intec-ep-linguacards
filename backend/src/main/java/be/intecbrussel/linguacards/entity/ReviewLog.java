package be.intecbrussel.linguacards.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "review_logs", indexes = {
        @Index(name = "idx_review_logs_card_id", columnList = "card_id"),
        @Index(name = "idx_review_logs_reviewed_at", columnList = "reviewed_at")
})
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_logs_card"))
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReviewRating rating;

    @Column(nullable = false, name = "reviewed_at")
    private Instant reviewedAt;

    protected ReviewLog() {
    }

    public ReviewLog(Card card, ReviewRating rating, Instant reviewedAt) {
        this.card = card;
        this.rating = rating;
        this.reviewedAt = reviewedAt;
    }

    public Long getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public ReviewRating getRating() {
        return rating;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }
}