package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.entity.ReviewLog;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import be.intecbrussel.linguacards.repository.ReviewLogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TrainingService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final ReviewLogRepository reviewLogRepository;

    public TrainingService(DeckRepository deckRepository, CardRepository cardRepository, ReviewLogRepository reviewLogRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.reviewLogRepository = reviewLogRepository;
    }

    public List<Card> getTrainingCards(Long ownerId, Long deckId, int limit) {
        deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        List<Card> cards = new ArrayList<>(cardRepository.findAllByDeckId(deckId));
        Collections.shuffle(cards);

        int effectiveLimit = limit <= 0 ? 20 : limit;
        return cards.subList(0, Math.min(effectiveLimit, cards.size()));
    }

    public ReviewLog logReview(Long ownerId, Long deckId, Long cardId, ReviewLog.Rating rating) {
        deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        Card card = cardRepository.findByIdAndDeckId(cardId, deckId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        ReviewLog reviewLog = ReviewLog.builder()
                .card(card)
                .rating(rating)
                .build();

        return reviewLogRepository.save(reviewLog);
    }
}
