package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.dto.DeckStatsResponse;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.exception.ResourceNotFoundException;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DeckStatsService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public DeckStatsService(DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public DeckStatsResponse getMyDeckStats(Long userId, Long deckId) {
        Deck deck = deckRepository.findByIdAndOwnerId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + deckId));

        Instant now = Instant.now();

        long total = cardRepository.countByDeckId(deck.getId());
        long newCards = cardRepository.countByDeckIdAndNextReviewAtIsNull(deck.getId());
        long due = cardRepository.countByDeckIdAndNextReviewAtIsNotNullAndNextReviewAtLessThanEqual(deck.getId(), now);
        long scheduled = cardRepository.countByDeckIdAndNextReviewAtIsNotNullAndNextReviewAtGreaterThan(deck.getId(), now);

        return new DeckStatsResponse(deck.getId(), total, newCards, due, scheduled);
    }
}