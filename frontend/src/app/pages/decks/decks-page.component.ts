import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DecksService } from '../../core/decks.service';
import { Card, Deck, DeckStats } from '../../models/api.models';

@Component({
  selector: 'app-decks-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './decks-page.component.html',
  styleUrl: './decks-page.component.scss'
})
export class DecksPageComponent implements OnInit {
  decks: Deck[] = [];
  selectedDeck: Deck | null = null;
  cards: Card[] = [];
  stats: DeckStats | null = null;
  message = '';

  readonly deckForm = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    languageCode: ['en', [Validators.required]],
    isPrivate: [true]
  });

  readonly cardForm = this.fb.nonNullable.group({
    term: ['', Validators.required],
    definition: ['', Validators.required],
    example: [''],
    cefrLevel: ['A1'],
    tags: ['']
  });

  constructor(private readonly fb: FormBuilder, private readonly decksService: DecksService) {}

  ngOnInit(): void {
    this.loadDecks();
  }

  loadDecks(): void {
    this.decksService.getMyDecks().subscribe({
      next: (decks) => this.decks = decks,
      error: () => this.message = 'Unable to load decks. Please login first.'
    });
  }

  createDeck(): void {
    if (this.deckForm.invalid) {
      this.deckForm.markAllAsTouched();
      return;
    }

    this.decksService.createDeck(this.deckForm.getRawValue()).subscribe({
      next: () => {
        this.deckForm.reset({ name: '', languageCode: 'en', isPrivate: true });
        this.loadDecks();
      },
      error: () => this.message = 'Failed to create deck.'
    });
  }

  selectDeck(deck: Deck): void {
    this.selectedDeck = deck;
    this.decksService.getCards(deck.id).subscribe((cards) => this.cards = cards);
    this.decksService.getStats(deck.id).subscribe((stats) => this.stats = stats);
  }

  addCard(): void {
    if (!this.selectedDeck || this.cardForm.invalid) {
      return;
    }

    this.decksService.addCard(this.selectedDeck.id, this.cardForm.getRawValue()).subscribe({
      next: () => {
        this.cardForm.reset({ term: '', definition: '', example: '', cefrLevel: 'A1', tags: '' });
        this.selectDeck(this.selectedDeck as Deck);
      },
      error: () => this.message = 'Failed to add card.'
    });
  }
}
