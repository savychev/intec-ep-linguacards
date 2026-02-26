package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.dto.TrainingCardResponse;
import be.intecbrussel.linguacards.dto.TrainingNextResponse;
import be.intecbrussel.linguacards.entity.*;
import be.intecbrussel.linguacards.exception.ResourceNotFoundException;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import be.intecbrussel.linguacards.repository.ReviewLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TrainingService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final ReviewLogRepository reviewLogRepository;

    public TrainingService(DeckRepository deckRepository,
                           CardRepository cardRepository,
                           ReviewLogRepository reviewLogRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.reviewLogRepository = reviewLogRepository;
    }

    @Transactional(readOnly = true)
    public TrainingNextResponse nextCardResult(Long userId, Long deckId) {
        Deck deck = requireMyDeck(userId, deckId);

        long total = cardRepository.countByDeckId(deck.getId());
        if (total == 0) {
            return TrainingNextResponse.noCard("EMPTY_DECK");
        }

        // 1) new cards first
        Card newCard = cardRepository.findFirstByDeckIdAndNextReviewAtIsNullOrderByIdAsc(deck.getId()).orElse(null);
        if (newCard != null) {
            return TrainingNextResponse.withCard(toResponse(newCard));
        }

        // 2) due cards
        List<Card> due = cardRepository.findDueCards(deck.getId(), Instant.now());
        if (!due.isEmpty()) {
            return TrainingNextResponse.withCard(toResponse(due.get(0)));
        }

        // 3) nothing to train right now
        return TrainingNextResponse.noCard("NO_CARDS_TO_TRAIN");
    }

    @Transactional
    public TrainingCardResponse reviewCard(Long userId, Long cardId, ReviewRating rating) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));

        // owner-check via deck
        requireMyDeck(userId, card.getDeck().getId());

        Instant now = Instant.now();

        int days = switch (rating) {
            case AGAIN -> 1;
            case HARD -> 3;
            case GOOD -> 7;
            case EASY -> 14;
        };

        card.setLastReviewedAt(now);
        card.setIntervalDays(days);
        card.setNextReviewAt(now.plus(days, ChronoUnit.DAYS));

        ReviewLog log = new ReviewLog(card, rating, now);
        reviewLogRepository.save(log);

        return toResponse(card);
    }

    private Deck requireMyDeck(Long userId, Long deckId) {
        return deckRepository.findByIdAndOwnerId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + deckId));
    }

    private TrainingCardResponse toResponse(Card card) {
        return new TrainingCardResponse(
                card.getId(),
                card.getDeck().getId(),
                card.getTerm(),
                card.getDefinition(),
                card.getExample(),
                card.getLastReviewedAt(),
                card.getNextReviewAt(),
                card.getIntervalDays()
        );
    }
}