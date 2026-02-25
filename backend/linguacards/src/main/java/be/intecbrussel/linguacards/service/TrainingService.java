package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.dto.TrainingCardResponse;
import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.exception.ResourceNotFoundException;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TrainingService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public TrainingService(DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public TrainingCardResponse nextCard(Long userId, Long deckId) {
        Deck deck = requireMyDeck(userId, deckId);

        // 1) new cards first
        Card newCard = cardRepository.findFirstByDeckIdAndNextReviewAtIsNullOrderByIdAsc(deck.getId()).orElse(null);
        if (newCard != null) {
            return toResponse(newCard);
        }

        // 2) due cards
        List<Card> due = cardRepository.findDueCards(deck.getId(), Instant.now());
        if (!due.isEmpty()) {
            return toResponse(due.get(0));
        }

        // 3) nothing to train
        return null;
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
                card.getNextReviewAt(),
                card.getIntervalDays()
        );
    }
}