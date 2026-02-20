import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Card, Deck, DeckStats } from '../models/api.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DecksService {
  constructor(private readonly http: HttpClient) {}

  getMyDecks(): Observable<Deck[]> {
    return this.http.get<Deck[]>(`${environment.apiBaseUrl}/decks/me`);
  }

  createDeck(payload: { name: string; languageCode: string; isPrivate: boolean }): Observable<Deck> {
    return this.http.post<Deck>(`${environment.apiBaseUrl}/decks/me`, payload);
  }

  getCards(deckId: number): Observable<Card[]> {
    return this.http.get<Card[]>(`${environment.apiBaseUrl}/decks/me/${deckId}/cards`);
  }

  addCard(deckId: number, payload: Omit<Card, 'id'>): Observable<Card> {
    return this.http.post<Card>(`${environment.apiBaseUrl}/decks/me/${deckId}/cards`, payload);
  }

  getTrainingCards(deckId: number, limit = 20): Observable<Card[]> {
    return this.http.get<Card[]>(`${environment.apiBaseUrl}/decks/me/${deckId}/training`, { params: { limit } });
  }

  reviewCard(deckId: number, cardId: number, rating: 'AGAIN' | 'HARD' | 'GOOD' | 'EASY'): Observable<unknown> {
    return this.http.post(`${environment.apiBaseUrl}/decks/me/${deckId}/cards/${cardId}/review`, { rating });
  }

  getStats(deckId: number): Observable<DeckStats> {
    return this.http.get<DeckStats>(`${environment.apiBaseUrl}/decks/me/${deckId}/stats`);
  }
}
