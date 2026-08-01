import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { DeckStats } from '../../../core/api/api.models';

@Injectable({ providedIn: 'root' })
export class DeckStatisticsService {
  private readonly http = inject(HttpClient);
  private readonly deckEndpoint = `${environment.apiBaseUrl}/api/decks`;

  get(deckId: number): Observable<DeckStats> {
    return this.http.get<DeckStats>(`${this.deckEndpoint}/${deckId}/stats`);
  }
}
