package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.dto.CardCreateRequest;
import be.intecbrussel.linguacards.dto.CardResponse;
import be.intecbrussel.linguacards.dto.CardUpdateRequest;
import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.exception.DuplicateTermException;
import be.intecbrussel.linguacards.exception.ResourceNotFoundException;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CardService(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    @Transactional(readOnly = true)
    public List<CardResponse> listCards(Long userId, Long deckId) {
        Deck deck = requireMyDeck(userId, deckId);

        return cardRepository.findAllByDeckId(deck.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CardResponse createCard(Long userId, Long deckId, CardCreateRequest req) {
        Deck deck = requireMyDeck(userId, deckId);

        String term = req.getTerm().trim();

        if (cardRepository.existsByDeckIdAndTermIgnoreCase(deck.getId(), term)) {
            throw new DuplicateTermException(term);
        }

        Card card = new Card(deck, term, req.getDefinition().trim());
        card.setExample(trimOrNull(req.getExample()));
        card.setCefr(trimOrNull(req.getCefr()));
        card.setTags(trimOrNull(req.getTags()));

        Card saved = cardRepository.save(card);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CardResponse getCard(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));

        // owner-check: через deck
        requireMyDeck(userId, card.getDeck().getId());

        return toResponse(card);
    }

    @Transactional
    public CardResponse updateCard(Long userId, Long cardId, CardUpdateRequest req) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));

        Deck deck = requireMyDeck(userId, card.getDeck().getId());

        String term = req.getTerm().trim();

        if (cardRepository.existsByDeckIdAndTermIgnoreCaseAndIdNot(deck.getId(), term, card.getId())) {
            throw new DuplicateTermException(term);
        }

        card.setTerm(term);
        card.setDefinition(req.getDefinition().trim());
        card.setExample(trimOrNull(req.getExample()));
        card.setCefr(trimOrNull(req.getCefr()));
        card.setTags(trimOrNull(req.getTags()));

        return toResponse(card);
    }

    @Transactional
    public void deleteCard(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));

        requireMyDeck(userId, card.getDeck().getId());

        cardRepository.delete(card);
    }

    private Deck requireMyDeck(Long userId, Long deckId) {
        return deckRepository.findByIdAndOwnerId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + deckId));
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getDeck().getId(),
                card.getTerm(),
                card.getDefinition(),
                card.getExample(),
                card.getCefr(),
                card.getTags()
        );
    }

    private String trimOrNull(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isBlank() ? null : t;
    }
}