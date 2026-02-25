package be.intecbrussel.linguacards.repository;

import be.intecbrussel.linguacards.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    List<Deck> findAllByOwnerId(Long ownerId);

    Optional<Deck> findByIdAndOwnerId(Long id, Long ownerId);
}