package be.intecbrussel.linguacards.repository;

import be.intecbrussel.linguacards.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    List<Deck> findAllByOwnerId(Long ownerId);
}