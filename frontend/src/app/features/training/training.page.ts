import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin, switchMap, tap } from 'rxjs';

import { apiErrorMessage } from '../../core/api/api-error';
import {
  Deck,
  ReviewRating,
  TrainingCard,
  TrainingEmptyReason,
  TrainingNextResponse,
} from '../../core/api/api.models';
import { DeckService } from '../decks/data-access/deck.service';
import { TrainingService } from './data-access/training.service';

@Component({
  selector: 'app-training-page',
  imports: [RouterLink],
  templateUrl: './training.page.html',
  styleUrl: './training.page.css',
})
export class TrainingPage implements OnInit {
  private readonly deckService = inject(DeckService);
  private readonly trainingService = inject(TrainingService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);

  protected readonly deckId = this.readDeckId();
  protected readonly deck = signal<Deck | null>(null);
  protected readonly card = signal<TrainingCard | null>(null);
  protected readonly emptyReason = signal<TrainingEmptyReason | null>(null);
  protected readonly answerRevealed = signal(false);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);
  protected readonly advancing = signal(false);
  protected readonly actionError = signal<string | null>(null);
  protected readonly ratings: ReadonlyArray<{
    value: ReviewRating;
    label: string;
    interval: string;
    tone: string;
  }> = [
    { value: 'AGAIN', label: 'Again', interval: '1 day', tone: 'again' },
    { value: 'HARD', label: 'Hard', interval: '3 days', tone: 'hard' },
    { value: 'GOOD', label: 'Good', interval: '7 days', tone: 'good' },
    { value: 'EASY', label: 'Easy', interval: '14 days', tone: 'easy' },
  ];

  ngOnInit(): void {
    this.startSession();
  }

  protected startSession(): void {
    if (this.deckId === null) {
      this.loading.set(false);
      this.loadError.set('This training address is missing a valid deck.');
      return;
    }

    this.loading.set(true);
    this.loadError.set(null);
    this.actionError.set(null);

    forkJoin({
      deck: this.deckService.get(this.deckId),
      session: this.trainingService.start(this.deckId),
    })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: ({ deck, session }) => {
          this.deck.set(deck);
          this.applySession(session);
        },
        error: (error: unknown) =>
          this.loadError.set(apiErrorMessage(error, 'Unable to start this training session.')),
      });
  }

  protected revealAnswer(): void {
    this.answerRevealed.set(true);
  }

  protected rate(rating: ReviewRating): void {
    const currentCard = this.card();
    if (this.deckId === null || !currentCard || this.advancing()) {
      return;
    }

    let reviewSaved = false;
    this.advancing.set(true);
    this.actionError.set(null);

    this.trainingService
      .review(currentCard.cardId, rating)
      .pipe(
        tap(() => {
          reviewSaved = true;
          this.card.set(null);
          this.emptyReason.set(null);
          this.answerRevealed.set(false);
        }),
        switchMap(() => this.trainingService.next(this.deckId!)),
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.advancing.set(false)),
      )
      .subscribe({
        next: (session) => this.applySession(session),
        error: (error: unknown) =>
          this.actionError.set(
            reviewSaved
              ? 'Your review was saved, but the next card could not be loaded.'
              : apiErrorMessage(error, 'Unable to save this review.'),
          ),
      });
  }

  protected retryNext(): void {
    if (this.deckId === null || this.advancing()) {
      return;
    }

    this.advancing.set(true);
    this.actionError.set(null);

    this.trainingService
      .next(this.deckId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.advancing.set(false)),
      )
      .subscribe({
        next: (session) => this.applySession(session),
        error: (error: unknown) =>
          this.actionError.set(apiErrorMessage(error, 'Unable to load the next card.')),
      });
  }

  private applySession(session: TrainingNextResponse): void {
    this.card.set(session.hasCard ? (session.card ?? null) : null);
    this.emptyReason.set(session.hasCard ? null : (session.reason ?? 'NO_CARDS_TO_TRAIN'));
    this.answerRevealed.set(false);
    this.actionError.set(null);
  }

  private readDeckId(): number | null {
    const rawValue = this.route.snapshot.queryParamMap.get('deckId');
    const value = rawValue === null ? Number.NaN : Number(rawValue);
    return Number.isSafeInteger(value) && value > 0 ? value : null;
  }
}
