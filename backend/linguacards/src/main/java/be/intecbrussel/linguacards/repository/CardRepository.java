package be.intecbrussel.linguacards.repository;

import be.intecbrussel.linguacards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByDeckId(Long deckId);

    Optional<Card> findByIdAndDeckId(Long id, Long deckId);

    boolean existsByDeckIdAndTermIgnoreCase(Long deckId, String term);

    boolean existsByDeckIdAndTermIgnoreCaseAndIdNot(Long deckId, String term, Long id);

    // Training picks:
    Optional<Card> findFirstByDeckIdAndNextReviewAtIsNullOrderByIdAsc(Long deckId);

    @Query("""
           select c
           from Card c
           where c.deck.id = :deckId
             and c.nextReviewAt is not null
             and c.nextReviewAt <= :now
           order by c.nextReviewAt asc, c.id asc
           """)
    List<Card> findDueCards(Long deckId, Instant now);

    // Stats:
    long countByDeckId(Long deckId);

    long countByDeckIdAndNextReviewAtIsNull(Long deckId);

    long countByDeckIdAndNextReviewAtIsNotNullAndNextReviewAtLessThanEqual(Long deckId, Instant now);

    long countByDeckIdAndNextReviewAtIsNotNullAndNextReviewAtGreaterThan(Long deckId, Instant now);
}