package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.entity.Card;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CardService(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public List<Card> getCards(Long ownerId, Long deckId) {
        deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        return cardRepository.findAllByDeckId(deckId);
    }

    public Card createCard(Long ownerId, Long deckId, String term, String definition, String example, String cefrLevel, String tags) {
        Deck deck = deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        if (cardRepository.existsByDeckIdAndTermIgnoreCase(deckId, term)) {
            throw new IllegalArgumentException("Duplicate term in deck");
        }

        Card card = Card.builder()
                .term(term)
                .definition(definition)
                .example(example)
                .cefrLevel(cefrLevel)
                .tags(tags)
                .deck(deck)
                .build();

        return cardRepository.save(card);
    }

    public Card updateCard(Long ownerId, Long deckId, Long cardId, String term, String definition, String example, String cefrLevel, String tags) {
        deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        Card card = cardRepository.findByIdAndDeckId(cardId, deckId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        boolean termChanged = !card.getTerm().equalsIgnoreCase(term);
        if (termChanged && cardRepository.existsByDeckIdAndTermIgnoreCase(deckId, term)) {
            throw new IllegalArgumentException("Duplicate term in deck");
        }

        card.setTerm(term);
        card.setDefinition(definition);
        card.setExample(example);
        card.setCefrLevel(cefrLevel);
        card.setTags(tags);

        return cardRepository.save(card);
    }

    public void deleteCard(Long ownerId, Long deckId, Long cardId) {
        deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));

        Card card = cardRepository.findByIdAndDeckId(cardId, deckId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        cardRepository.delete(card);
    }
}
