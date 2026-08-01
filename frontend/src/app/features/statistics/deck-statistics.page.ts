import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';

import { apiErrorMessage } from '../../core/api/api-error';
import { Deck, DeckStats } from '../../core/api/api.models';
import { DeckService } from '../decks/data-access/deck.service';
import { DeckStatisticsService } from './data-access/deck-statistics.service';

interface DistributionSegment {
  label: string;
  tone: string;
  value: number;
  percentage: number;
}

@Component({
  selector: 'app-deck-statistics-page',
  imports: [RouterLink],
  templateUrl: './deck-statistics.page.html',
  styleUrl: './deck-statistics.page.css',
})
export class DeckStatisticsPage implements OnInit {
  private readonly deckService = inject(DeckService);
  private readonly statisticsService = inject(DeckStatisticsService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);

  protected readonly deckId = this.readDeckId();
  protected readonly deck = signal<Deck | null>(null);
  protected readonly stats = signal<DeckStats | null>(null);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);
  protected readonly readyCards = computed(() => {
    const currentStats = this.stats();
    return currentStats ? currentStats.newCards + currentStats.dueCards : 0;
  });
  protected readonly distribution = computed<DistributionSegment[]>(() => {
    const currentStats = this.stats();
    if (!currentStats || currentStats.totalCards === 0) {
      return [];
    }

    return [
      this.segment('New', 'new', currentStats.newCards, currentStats.totalCards),
      this.segment('Due now', 'due', currentStats.dueCards, currentStats.totalCards),
      this.segment('Scheduled', 'scheduled', currentStats.scheduledCards, currentStats.totalCards),
    ];
  });
  protected readonly distributionLabel = computed(() => {
    const currentStats = this.stats();
    return currentStats
      ? `${currentStats.newCards} new, ${currentStats.dueCards} due now, ${currentStats.scheduledCards} scheduled for later.`
      : '';
  });

  ngOnInit(): void {
    this.loadPage();
  }

  protected loadPage(): void {
    if (this.deckId === null) {
      this.loading.set(false);
      this.loadError.set('This statistics address is missing a valid deck.');
      return;
    }

    this.loading.set(true);
    this.loadError.set(null);
    this.deck.set(null);
    this.stats.set(null);

    forkJoin({
      deck: this.deckService.get(this.deckId),
      stats: this.statisticsService.get(this.deckId),
    })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: ({ deck, stats }) => {
          this.deck.set(deck);
          this.stats.set(stats);
        },
        error: (error: unknown) =>
          this.loadError.set(apiErrorMessage(error, 'Unable to load these deck statistics.')),
      });
  }

  private segment(label: string, tone: string, value: number, total: number): DistributionSegment {
    return { label, tone, value, percentage: (value / total) * 100 };
  }

  private readDeckId(): number | null {
    const rawValue = this.route.snapshot.queryParamMap.get('deckId');
    const value = rawValue === null ? Number.NaN : Number(rawValue);
    return Number.isSafeInteger(value) && value > 0 ? value : null;
  }
}
