package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.entity.ReviewLog;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import be.intecbrussel.linguacards.repository.ReviewLogRepository;
import be.intecbrussel.linguacards.service.model.DeckStats;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final ReviewLogRepository reviewLogRepository;

    public StatsService(DeckRepository deckRepository, CardRepository cardRepository, ReviewLogRepository reviewLogRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.reviewLogRepository = reviewLogRepository;
    }

    public DeckStats getDeckStats(Long ownerId, Long deckId) {
        deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        long totalCards = cardRepository.countByDeckId(deckId);
        long totalReviews = reviewLogRepository.countByCardDeckId(deckId);
        long againCount = reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.AGAIN);
        long hardCount = reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.HARD);
        long goodCount = reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.GOOD);
        long easyCount = reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.EASY);

        return new DeckStats(totalCards, totalReviews, againCount, hardCount, goodCount, easyCount);
    }
}
