import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { Card, CardPayload } from '../../../core/api/api.models';

@Injectable({ providedIn: 'root' })
export class CardService {
  private readonly http = inject(HttpClient);
  private readonly apiRoot = `${environment.apiBaseUrl}/api`;

  list(deckId: number): Observable<Card[]> {
    return this.http.get<Card[]>(`${this.apiRoot}/decks/${deckId}/cards`);
  }

  get(cardId: number): Observable<Card> {
    return this.http.get<Card>(`${this.apiRoot}/cards/${cardId}`);
  }

  create(deckId: number, payload: CardPayload): Observable<Card> {
    return this.http.post<Card>(`${this.apiRoot}/decks/${deckId}/cards`, payload);
  }

  update(cardId: number, payload: CardPayload): Observable<Card> {
    return this.http.put<Card>(`${this.apiRoot}/cards/${cardId}`, payload);
  }

  delete(cardId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiRoot}/cards/${cardId}`);
  }
}
