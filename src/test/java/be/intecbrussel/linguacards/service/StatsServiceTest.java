package be.intecbrussel.linguacards.service;

import be.intecbrussel.linguacards.entity.Deck;
import be.intecbrussel.linguacards.entity.ReviewLog;
import be.intecbrussel.linguacards.repository.CardRepository;
import be.intecbrussel.linguacards.repository.DeckRepository;
import be.intecbrussel.linguacards.repository.ReviewLogRepository;
import be.intecbrussel.linguacards.service.model.DeckStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ReviewLogRepository reviewLogRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    void getDeckStatsShouldUseCountQueryForTotalCards() {
        long ownerId = 10L;
        long deckId = 100L;

        when(deckRepository.findByIdAndOwnerId(deckId, ownerId)).thenReturn(Optional.of(Deck.builder().id(deckId).build()));
        when(cardRepository.countByDeckId(deckId)).thenReturn(42L);
        when(reviewLogRepository.countByCardDeckId(deckId)).thenReturn(11L);
        when(reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.AGAIN)).thenReturn(1L);
        when(reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.HARD)).thenReturn(2L);
        when(reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.GOOD)).thenReturn(3L);
        when(reviewLogRepository.countByCardDeckIdAndRating(deckId, ReviewLog.Rating.EASY)).thenReturn(5L);

        DeckStats stats = statsService.getDeckStats(ownerId, deckId);

        assertThat(stats.totalCards()).isEqualTo(42L);
        assertThat(stats.totalReviews()).isEqualTo(11L);
        assertThat(stats.againCount()).isEqualTo(1L);
        assertThat(stats.hardCount()).isEqualTo(2L);
        assertThat(stats.goodCount()).isEqualTo(3L);
        assertThat(stats.easyCount()).isEqualTo(5L);

        verify(cardRepository).countByDeckId(deckId);
        verify(cardRepository, never()).findAllByDeckId(deckId);
    }
}
