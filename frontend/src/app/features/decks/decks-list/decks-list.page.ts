import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { apiErrorMessage } from '../../../core/api/api-error';
import { Deck, DeckPayload } from '../../../core/api/api.models';
import { DeckService } from '../data-access/deck.service';

@Component({
  selector: 'app-decks-list-page',
  imports: [ReactiveFormsModule],
  templateUrl: './decks-list.page.html',
  styleUrl: './decks-list.page.css',
})
export class DecksListPage implements OnInit {
  private readonly deckService = inject(DeckService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly decks = signal<Deck[]>([]);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);
  protected readonly editorOpen = signal(false);
  protected readonly editingDeck = signal<Deck | null>(null);
  protected readonly saving = signal(false);
  protected readonly editorError = signal<string | null>(null);
  protected readonly confirmingDeleteId = signal<number | null>(null);
  protected readonly deletingDeckId = signal<number | null>(null);
  protected readonly actionError = signal<string | null>(null);

  protected readonly languageSuggestions = [
    { code: 'nl', name: 'Dutch' },
    { code: 'en', name: 'English' },
    { code: 'fr', name: 'French' },
    { code: 'pt', name: 'Portuguese' },
    { code: 'de', name: 'German' },
    { code: 'es', name: 'Spanish' },
    { code: 'uk', name: 'Ukrainian' },
    { code: 'ru', name: 'Russian' },
  ];

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.pattern(/.*\S.*/), Validators.maxLength(120)]],
    languageCode: [
      '',
      [
        Validators.required,
        Validators.pattern(/.*\S.*/),
        Validators.minLength(2),
        Validators.maxLength(10),
      ],
    ],
  });

  ngOnInit(): void {
    this.loadDecks();
  }

  protected loadDecks(): void {
    this.loading.set(true);
    this.loadError.set(null);

    this.deckService
      .list()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (decks) => this.decks.set(decks),
        error: (error: unknown) =>
          this.loadError.set(apiErrorMessage(error, 'Unable to load your decks.')),
      });
  }

  protected openCreate(): void {
    this.editingDeck.set(null);
    this.editorError.set(null);
    this.form.reset({ name: '', languageCode: '' });
    this.editorOpen.set(true);
  }

  protected openEdit(deck: Deck): void {
    this.editingDeck.set(deck);
    this.editorError.set(null);
    this.form.reset({ name: deck.name, languageCode: deck.languageCode });
    this.editorOpen.set(true);
  }

  protected closeEditor(): void {
    if (this.saving()) {
      return;
    }

    this.resetEditor();
  }

  private resetEditor(): void {
    this.editorOpen.set(false);
    this.editingDeck.set(null);
    this.editorError.set(null);
  }

  protected save(): void {
    if (this.form.invalid || this.saving()) {
      this.form.markAllAsTouched();
      return;
    }

    const rawValue = this.form.getRawValue();
    const payload: DeckPayload = {
      name: rawValue.name.trim(),
      languageCode: rawValue.languageCode.trim().toLowerCase(),
      isPrivate: true,
    };

    if (!payload.name || !payload.languageCode) {
      this.form.markAllAsTouched();
      return;
    }

    const currentDeck = this.editingDeck();
    const request = currentDeck
      ? this.deckService.update(currentDeck.id, payload)
      : this.deckService.create(payload);

    this.saving.set(true);
    this.editorError.set(null);

    request
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: (savedDeck) => {
          this.decks.update((decks) =>
            currentDeck
              ? decks.map((deck) => (deck.id === savedDeck.id ? savedDeck : deck))
              : [...decks, savedDeck],
          );
          this.resetEditor();
        },
        error: (error: unknown) =>
          this.editorError.set(apiErrorMessage(error, 'Unable to save this deck.')),
      });
  }

  protected requestDelete(deck: Deck): void {
    this.actionError.set(null);
    this.confirmingDeleteId.set(deck.id);
  }

  protected cancelDelete(): void {
    if (this.deletingDeckId() === null) {
      this.confirmingDeleteId.set(null);
    }
  }

  protected deleteDeck(deck: Deck): void {
    if (this.deletingDeckId() !== null) {
      return;
    }

    this.deletingDeckId.set(deck.id);
    this.actionError.set(null);

    this.deckService
      .delete(deck.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.deletingDeckId.set(null)),
      )
      .subscribe({
        next: () => {
          this.decks.update((decks) => decks.filter((item) => item.id !== deck.id));
          this.confirmingDeleteId.set(null);
        },
        error: (error: unknown) => {
          this.confirmingDeleteId.set(null);
          this.actionError.set(apiErrorMessage(error, `Unable to delete “${deck.name}”.`));
        },
      });
  }
}
