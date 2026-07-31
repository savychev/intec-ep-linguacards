import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { Deck, DeckPayload } from '../../../core/api/api.models';

@Injectable({ providedIn: 'root' })
export class DeckService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiBaseUrl}/api/decks`;

  list(): Observable<Deck[]> {
    return this.http.get<Deck[]>(this.endpoint);
  }

  get(id: number): Observable<Deck> {
    return this.http.get<Deck>(`${this.endpoint}/${id}`);
  }

  create(payload: DeckPayload): Observable<Deck> {
    return this.http.post<Deck>(this.endpoint, payload);
  }

  update(id: number, payload: DeckPayload): Observable<Deck> {
    return this.http.put<Deck>(`${this.endpoint}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
}
