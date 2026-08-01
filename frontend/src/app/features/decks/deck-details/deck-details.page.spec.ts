import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { Card, Deck } from '../../../core/api/api.models';
import { CardService } from '../data-access/card.service';
import { DeckService } from '../data-access/deck.service';
import { DeckDetailsPage } from './deck-details.page';

describe('DeckDetailsPage', () => {
  const deck: Deck = {
    id: 1,
    name: 'Dutch B2',
    languageCode: 'nl',
    isPrivate: true,
  };
  const card: Card = {
    id: 10,
    deckId: deck.id,
    term: 'gezellig',
    definition: 'Warm and pleasantly sociable.',
    example: 'Het was een gezellige avond.',
    cefr: 'B1',
    tags: 'adjective',
  };
  const deckService = { get: vi.fn() };
  const cardService = {
    list: vi.fn(),
    get: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn(),
  };

  let harness: RouterTestingHarness;

  beforeEach(async () => {
    deckService.get.mockReset().mockReturnValue(of(deck));
    cardService.list.mockReset().mockReturnValue(of([card]));
    cardService.create.mockReset();
    cardService.update.mockReset();
    cardService.delete.mockReset();

    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'decks/:id', component: DeckDetailsPage }]),
        { provide: DeckService, useValue: deckService },
        { provide: CardService, useValue: cardService },
      ],
    });

    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/decks/1', DeckDetailsPage);
    harness.detectChanges();
  });

  it('loads and renders the deck with its cards', () => {
    expect(deckService.get).toHaveBeenCalledWith(deck.id);
    expect(cardService.list).toHaveBeenCalledWith(deck.id);
    expect(root().textContent).toContain('Dutch B2');
    expect(root().textContent).toContain('gezellig');
  });

  it('links a non-empty deck to its training session and statistics', () => {
    const links = [...root().querySelectorAll<HTMLAnchorElement>('a')];
    const trainingLink = links.find(
      (candidate) => candidate.textContent?.trim() === 'Start training',
    );
    const statisticsLink = links.find(
      (candidate) => candidate.textContent?.trim() === 'View statistics',
    );

    expect(trainingLink?.getAttribute('href')).toBe('/training?deckId=1');
    expect(statisticsLink?.getAttribute('href')).toBe('/stats?deckId=1');
  });

  it('creates a normalized card and adds it to the deck', () => {
    const created: Card = {
      id: 11,
      deckId: deck.id,
      term: 'uitbreiding',
      definition: 'Het groter maken van iets.',
      example: null,
      cefr: 'B2',
      tags: null,
    };
    cardService.create.mockReturnValue(of(created));

    buttonByText('Add card').click();
    harness.detectChanges();
    setInput('#card-term', '  uitbreiding  ');
    setInput('#card-definition', '  Het groter maken van iets.  ');
    setInput('#card-cefr', 'B2');
    submitEditor();

    expect(cardService.create).toHaveBeenCalledWith(deck.id, {
      term: 'uitbreiding',
      definition: 'Het groter maken van iets.',
      example: null,
      cefr: 'B2',
      tags: null,
    });
    expect(root().textContent).toContain('uitbreiding');
  });

  it('updates an existing card', () => {
    const updated = { ...card, definition: 'A pleasant feeling of togetherness.' };
    cardService.update.mockReturnValue(of(updated));

    buttonByText('Edit').click();
    harness.detectChanges();
    setInput('#card-definition', 'A pleasant feeling of togetherness.');
    submitEditor();

    expect(cardService.update).toHaveBeenCalledWith(card.id, {
      term: card.term,
      definition: 'A pleasant feeling of togetherness.',
      example: card.example,
      cefr: card.cefr,
      tags: card.tags,
    });
    expect(root().textContent).toContain('A pleasant feeling of togetherness.');
  });

  it('requires confirmation before deleting a card', () => {
    cardService.delete.mockReturnValue(of(undefined));

    buttonByText('Delete').click();
    harness.detectChanges();
    expect(cardService.delete).not.toHaveBeenCalled();

    buttonByText('Delete permanently').click();
    harness.detectChanges();

    expect(cardService.delete).toHaveBeenCalledWith(card.id);
    expect(root().textContent).not.toContain('gezellig');
  });

  function root(): HTMLElement {
    const element = harness.routeNativeElement;
    if (!element) {
      throw new Error('Routed component was not rendered');
    }
    return element;
  }

  function buttonByText(text: string): HTMLButtonElement {
    const button = [...root().querySelectorAll('button')].find(
      (candidate) => candidate.textContent?.trim() === text,
    );
    if (!button) {
      throw new Error(`Button not found: ${text}`);
    }
    return button;
  }

  function setInput(selector: string, value: string): void {
    const control = root().querySelector<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >(selector);
    if (!control) {
      throw new Error(`Form control not found: ${selector}`);
    }
    control.value = value;
    control.dispatchEvent(new Event('input'));
    control.dispatchEvent(new Event('change'));
    harness.detectChanges();
  }

  function submitEditor(): void {
    const form = root().querySelector<HTMLFormElement>('form');
    if (!form) {
      throw new Error('Card form not found');
    }
    form.dispatchEvent(new Event('submit'));
    harness.detectChanges();
  }
});
