package be.intecbrussel.linguacards.repository;

import be.intecbrussel.linguacards.entity.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {
    List<ReviewLog> findAllByCardIdOrderByReviewedAtDesc(Long cardId);

    long countByCardDeckId(Long deckId);

    long countByCardDeckIdAndRating(Long deckId, ReviewLog.Rating rating);
}
