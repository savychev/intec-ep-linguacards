import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';

import { apiErrorMessage } from '../../../core/api/api-error';
import { Card, CardPayload, Deck } from '../../../core/api/api.models';
import { CardService } from '../data-access/card.service';
import { DeckService } from '../data-access/deck.service';

@Component({
  selector: 'app-deck-details-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './deck-details.page.html',
  styleUrl: './deck-details.page.css',
})
export class DeckDetailsPage implements OnInit {
  private readonly cardService = inject(CardService);
  private readonly deckService = inject(DeckService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly deckId = this.readDeckId();

  protected readonly deck = signal<Deck | null>(null);
  protected readonly cards = signal<Card[]>([]);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);
  protected readonly editorOpen = signal(false);
  protected readonly editingCard = signal<Card | null>(null);
  protected readonly saving = signal(false);
  protected readonly editorError = signal<string | null>(null);
  protected readonly confirmingDeleteId = signal<number | null>(null);
  protected readonly deletingCardId = signal<number | null>(null);
  protected readonly actionError = signal<string | null>(null);
  protected readonly cefrOptions = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

  protected readonly form = this.formBuilder.nonNullable.group({
    term: ['', [Validators.required, Validators.pattern(/.*\S.*/), Validators.maxLength(200)]],
    definition: [
      '',
      [Validators.required, Validators.pattern(/.*\S.*/), Validators.maxLength(2000)],
    ],
    example: ['', Validators.maxLength(500)],
    cefr: ['', Validators.maxLength(5)],
    tags: ['', Validators.maxLength(200)],
  });

  ngOnInit(): void {
    this.loadPage();
  }

  protected loadPage(): void {
    if (this.deckId === null) {
      this.loading.set(false);
      this.loadError.set('This deck address is invalid.');
      return;
    }

    this.loading.set(true);
    this.loadError.set(null);

    forkJoin({
      deck: this.deckService.get(this.deckId),
      cards: this.cardService.list(this.deckId),
    })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: ({ deck, cards }) => {
          this.deck.set(deck);
          this.cards.set(cards);
        },
        error: (error: unknown) =>
          this.loadError.set(apiErrorMessage(error, 'Unable to load this deck.')),
      });
  }

  protected openCreate(): void {
    this.editingCard.set(null);
    this.editorError.set(null);
    this.form.reset({ term: '', definition: '', example: '', cefr: '', tags: '' });
    this.editorOpen.set(true);
  }

  protected openEdit(card: Card): void {
    this.editingCard.set(card);
    this.editorError.set(null);
    this.form.reset({
      term: card.term,
      definition: card.definition,
      example: card.example ?? '',
      cefr: card.cefr ?? '',
      tags: card.tags ?? '',
    });
    this.editorOpen.set(true);
  }

  protected closeEditor(): void {
    if (!this.saving()) {
      this.resetEditor();
    }
  }

  protected save(): void {
    if (this.deckId === null || this.form.invalid || this.saving()) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: CardPayload = {
      term: value.term.trim(),
      definition: value.definition.trim(),
      example: this.optional(value.example),
      cefr: this.optional(value.cefr)?.toUpperCase() ?? null,
      tags: this.optional(value.tags),
    };
    const currentCard = this.editingCard();
    const request = currentCard
      ? this.cardService.update(currentCard.id, payload)
      : this.cardService.create(this.deckId, payload);

    this.saving.set(true);
    this.editorError.set(null);

    request
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: (savedCard) => {
          this.cards.update((cards) =>
            currentCard
              ? cards.map((card) => (card.id === savedCard.id ? savedCard : card))
              : [...cards, savedCard],
          );
          this.resetEditor();
        },
        error: (error: unknown) =>
          this.editorError.set(apiErrorMessage(error, 'Unable to save this card.')),
      });
  }

  protected requestDelete(card: Card): void {
    this.actionError.set(null);
    this.confirmingDeleteId.set(card.id);
  }

  protected cancelDelete(): void {
    if (this.deletingCardId() === null) {
      this.confirmingDeleteId.set(null);
    }
  }

  protected deleteCard(card: Card): void {
    if (this.deletingCardId() !== null) {
      return;
    }

    this.deletingCardId.set(card.id);
    this.actionError.set(null);

    this.cardService
      .delete(card.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.deletingCardId.set(null)),
      )
      .subscribe({
        next: () => {
          this.cards.update((cards) => cards.filter((item) => item.id !== card.id));
          this.confirmingDeleteId.set(null);
        },
        error: (error: unknown) => {
          this.confirmingDeleteId.set(null);
          this.actionError.set(apiErrorMessage(error, `Unable to delete “${card.term}”.`));
        },
      });
  }

  private resetEditor(): void {
    this.editorOpen.set(false);
    this.editingCard.set(null);
    this.editorError.set(null);
  }

  private optional(value: string): string | null {
    return value.trim() || null;
  }

  private readDeckId(): number | null {
    const value = Number(this.route.snapshot.paramMap.get('id'));
    return Number.isSafeInteger(value) && value > 0 ? value : null;
  }
}
