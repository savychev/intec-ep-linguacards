import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../../environments/environment';
import { Card, CardPayload } from '../../../core/api/api.models';
import { CardService } from './card.service';

describe('CardService', () => {
  const apiRoot = `${environment.apiBaseUrl}/api`;
  const payload: CardPayload = {
    term: 'gezellig',
    definition: 'Warm and pleasantly sociable.',
    example: 'Het was een gezellige avond.',
    cefr: 'B1',
    tags: 'adjective',
  };
  const card: Card = { id: 9, deckId: 3, ...payload };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('lists cards in a deck', () => {
    TestBed.inject(CardService).list(card.deckId).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${apiRoot}/decks/${card.deckId}/cards`,
    );
    expect(request.request.method).toBe('GET');
    request.flush([card]);
  });

  it('gets one card by id', () => {
    TestBed.inject(CardService).get(card.id).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(`${apiRoot}/cards/${card.id}`);
    expect(request.request.method).toBe('GET');
    request.flush(card);
  });

  it('creates a card inside a deck', () => {
    TestBed.inject(CardService).create(card.deckId, payload).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${apiRoot}/decks/${card.deckId}/cards`,
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush(card, { status: 201, statusText: 'Created' });
  });

  it('updates a card by id', () => {
    TestBed.inject(CardService).update(card.id, payload).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(`${apiRoot}/cards/${card.id}`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);
    request.flush(card);
  });

  it('deletes a card by id', () => {
    TestBed.inject(CardService).delete(card.id).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(`${apiRoot}/cards/${card.id}`);
    expect(request.request.method).toBe('DELETE');
    request.flush(null, { status: 204, statusText: 'No Content' });
  });
});
