import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../../environments/environment';
import { Deck, DeckPayload } from '../../../core/api/api.models';
import { DeckService } from './deck.service';

describe('DeckService', () => {
  const endpoint = `${environment.apiBaseUrl}/api/decks`;
  const payload: DeckPayload = {
    name: 'Dutch B2',
    languageCode: 'nl',
    isPrivate: true,
  };
  const deck: Deck = { id: 7, ...payload };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('lists the authenticated user decks', () => {
    let result: Deck[] | undefined;
    TestBed.inject(DeckService)
      .list()
      .subscribe((decks) => (result = decks));

    const request = TestBed.inject(HttpTestingController).expectOne(endpoint);
    expect(request.request.method).toBe('GET');
    request.flush([deck]);

    expect(result).toEqual([deck]);
  });

  it('gets one deck by id', () => {
    TestBed.inject(DeckService).get(deck.id).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(`${endpoint}/${deck.id}`);
    expect(request.request.method).toBe('GET');
    request.flush(deck);
  });

  it('creates a deck', () => {
    TestBed.inject(DeckService).create(payload).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(endpoint);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush(deck, { status: 201, statusText: 'Created' });
  });

  it('updates a deck', () => {
    TestBed.inject(DeckService).update(deck.id, payload).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(`${endpoint}/${deck.id}`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);
    request.flush(deck);
  });

  it('deletes a deck', () => {
    TestBed.inject(DeckService).delete(deck.id).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(`${endpoint}/${deck.id}`);
    expect(request.request.method).toBe('DELETE');
    request.flush(null, { status: 204, statusText: 'No Content' });
  });
});
