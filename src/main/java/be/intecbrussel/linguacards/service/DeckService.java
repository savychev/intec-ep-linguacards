package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.repository.DeckRepository;
import be.intecbrussel.linguacards.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    public DeckService(DeckRepository deckRepository, UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    public List<Deck> getUserDecks(Long ownerId) {
        return deckRepository.findAllByOwnerId(ownerId);
    }

    public Deck createDeck(Long ownerId, String name, String languageCode, boolean isPrivate) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Deck deck = Deck.builder()
                .name(name)
                .languageCode(languageCode)
                .isPrivate(isPrivate)
                .owner(owner)
                .build();

        return deckRepository.save(deck);
    }

    public Deck getDeck(Long ownerId, Long deckId) {
        return deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found"));
    }

    public Deck updateDeck(Long ownerId, Long deckId, String name, String languageCode, boolean isPrivate) {
        Deck deck = getDeck(ownerId, deckId);
        deck.setName(name);
        deck.setLanguageCode(languageCode);
        deck.setPrivate(isPrivate);
        return deckRepository.save(deck);
    }

    public void deleteDeck(Long ownerId, Long deckId) {
        Deck deck = getDeck(ownerId, deckId);
        deckRepository.delete(deck);
    }
}
