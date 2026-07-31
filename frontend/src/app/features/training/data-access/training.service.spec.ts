import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../../environments/environment';
import { TrainingCard, TrainingNextResponse } from '../../../core/api/api.models';
import { TrainingService } from './training.service';

describe('TrainingService', () => {
  const apiRoot = `${environment.apiBaseUrl}/api`;
  const card: TrainingCard = {
    cardId: 9,
    deckId: 3,
    term: 'gezellig',
    definition: 'Warm and pleasantly sociable.',
    example: 'Het was een gezellige avond.',
    lastReviewedAt: null,
    nextReviewAt: null,
    intervalDays: 0,
  };
  const response: TrainingNextResponse = { hasCard: true, card };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('starts a deck training session', () => {
    TestBed.inject(TrainingService).start(card.deckId).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${apiRoot}/decks/${card.deckId}/train/start`,
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toBeNull();
    request.flush(response);
  });

  it('loads the next due card', () => {
    TestBed.inject(TrainingService).next(card.deckId).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${apiRoot}/decks/${card.deckId}/train/next`,
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toBeNull();
    request.flush(response);
  });

  it('reviews a card with one of the four ratings', () => {
    TestBed.inject(TrainingService).review(card.cardId, 'GOOD').subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${apiRoot}/cards/${card.cardId}/review`,
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ rating: 'GOOD' });
    request.flush({ ...card, intervalDays: 7 });
  });
});
