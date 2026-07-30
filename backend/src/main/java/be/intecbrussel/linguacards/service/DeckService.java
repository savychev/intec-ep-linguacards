package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.dto.DeckCreateRequest;
import be.intecbrussel.linguacards.dto.DeckResponse;
import be.intecbrussel.linguacards.dto.DeckUpdateRequest;
import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.entity.User;
import be.intecbrussel.linguacards.exception.ResourceNotFoundException;
import be.intecbrussel.linguacards.repository.DeckRepository;
import be.intecbrussel.linguacards.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    public DeckService(DeckRepository deckRepository, UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<DeckResponse> listMyDecks(Long userId) {
        return deckRepository.findAllByOwnerId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DeckResponse createDeck(Long userId, DeckCreateRequest req) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Deck deck = new Deck(
                owner,
                req.getName().trim(),
                req.getLanguageCode().trim(),
                req.getIsPrivate() != null ? req.getIsPrivate() : true
        );

        Deck saved = deckRepository.save(deck);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DeckResponse getMyDeck(Long userId, Long deckId) {
        Deck deck = deckRepository.findByIdAndOwnerId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + deckId));
        return toResponse(deck);
    }

    @Transactional
    public DeckResponse updateMyDeck(Long userId, Long deckId, DeckUpdateRequest req) {
        Deck deck = deckRepository.findByIdAndOwnerId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + deckId));

        deck.setName(req.getName().trim());
        deck.setLanguageCode(req.getLanguageCode().trim());
        deck.setPrivate(req.getIsPrivate() != null ? req.getIsPrivate() : true);

        return toResponse(deck);
    }

    @Transactional
    public void deleteMyDeck(Long userId, Long deckId) {
        Deck deck = deckRepository.findByIdAndOwnerId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + deckId));
        deckRepository.delete(deck);
    }

    private DeckResponse toResponse(Deck deck) {
        return new DeckResponse(
                deck.getId(),
                deck.getName(),
                deck.getLanguageCode(),
                deck.isPrivate()
        );
    }
}