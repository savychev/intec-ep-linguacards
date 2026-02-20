package be.intecbrussel.linguacards.repository;

import be.intecbrussel.linguacards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByDeckId(Long deckId);

    long countByDeckId(Long deckId);

    Optional<Card> findByIdAndDeckId(Long cardId, Long deckId);

    boolean existsByDeckIdAndTermIgnoreCase(Long deckId, String term);
}
