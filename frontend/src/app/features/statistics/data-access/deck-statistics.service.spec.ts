import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../../environments/environment';
import { DeckStats } from '../../../core/api/api.models';
import { DeckStatisticsService } from './deck-statistics.service';

describe('DeckStatisticsService', () => {
  const stats: DeckStats = {
    deckId: 7,
    totalCards: 20,
    newCards: 5,
    dueCards: 7,
    scheduledCards: 8,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('loads statistics for one owned deck', () => {
    let result: DeckStats | undefined;
    TestBed.inject(DeckStatisticsService)
      .get(stats.deckId)
      .subscribe((response) => (result = response));

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${environment.apiBaseUrl}/api/decks/${stats.deckId}/stats`,
    );
    expect(request.request.method).toBe('GET');
    request.flush(stats);

    expect(result).toEqual(stats);
  });
});
