import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { ReviewRating, TrainingCard, TrainingNextResponse } from '../../../core/api/api.models';

@Injectable({ providedIn: 'root' })
export class TrainingService {
  private readonly http = inject(HttpClient);
  private readonly apiRoot = `${environment.apiBaseUrl}/api`;

  start(deckId: number): Observable<TrainingNextResponse> {
    return this.http.post<TrainingNextResponse>(
      `${this.apiRoot}/decks/${deckId}/train/start`,
      null,
    );
  }

  next(deckId: number): Observable<TrainingNextResponse> {
    return this.http.post<TrainingNextResponse>(`${this.apiRoot}/decks/${deckId}/train/next`, null);
  }

  review(cardId: number, rating: ReviewRating): Observable<TrainingCard> {
    return this.http.post<TrainingCard>(`${this.apiRoot}/cards/${cardId}/review`, { rating });
  }
}
