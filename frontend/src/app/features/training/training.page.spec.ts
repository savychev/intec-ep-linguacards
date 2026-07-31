import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { Deck, TrainingCard, TrainingNextResponse } from '../../core/api/api.models';
import { DeckService } from '../decks/data-access/deck.service';
import { TrainingService } from './data-access/training.service';
import { TrainingPage } from './training.page';

describe('TrainingPage', () => {
  const deck: Deck = {
    id: 1,
    name: 'Dutch B2',
    languageCode: 'nl',
    isPrivate: true,
  };
  const card: TrainingCard = {
    cardId: 10,
    deckId: deck.id,
    term: 'gezellig',
    definition: 'Warm and pleasantly sociable.',
    example: 'Het was een gezellige avond.',
    lastReviewedAt: null,
    nextReviewAt: null,
    intervalDays: 0,
  };
  const deckService = { get: vi.fn() };
  const trainingService = {
    start: vi.fn(),
    next: vi.fn(),
    review: vi.fn(),
  };

  let harness: RouterTestingHarness;

  beforeEach(async () => {
    deckService.get.mockReset().mockReturnValue(of(deck));
    trainingService.start.mockReset();
    trainingService.next.mockReset();
    trainingService.review.mockReset();

    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'training', component: TrainingPage }]),
        { provide: DeckService, useValue: deckService },
        { provide: TrainingService, useValue: trainingService },
      ],
    });

    harness = await RouterTestingHarness.create();
  });

  it('starts a session and reveals the answer before showing ratings', async () => {
    await render({ hasCard: true, card });

    expect(deckService.get).toHaveBeenCalledWith(deck.id);
    expect(trainingService.start).toHaveBeenCalledWith(deck.id);
    expect(root().textContent).toContain('gezellig');
    expect(root().textContent).not.toContain(card.definition);

    buttonByText('Show answer').click();
    harness.detectChanges();

    expect(root().textContent).toContain(card.definition);
    expect(root().querySelectorAll('[data-rating]').length).toBe(4);
  });

  it('reviews the current card and advances to the next one', async () => {
    const nextCard = { ...card, cardId: 11, term: 'uitbreiding', definition: 'Growth in size.' };
    trainingService.review.mockReturnValue(of({ ...card, intervalDays: 7 }));
    trainingService.next.mockReturnValue(of({ hasCard: true, card: nextCard }));
    await render({ hasCard: true, card });

    buttonByText('Show answer').click();
    harness.detectChanges();
    ratingButton('GOOD').click();
    harness.detectChanges();

    expect(trainingService.review).toHaveBeenCalledWith(card.cardId, 'GOOD');
    expect(trainingService.next).toHaveBeenCalledWith(deck.id);
    expect(root().textContent).toContain('uitbreiding');
    expect(root().textContent).not.toContain(nextCard.definition);
  });

  it('directs the user to add cards when the deck is empty', async () => {
    await render({ hasCard: false, reason: 'EMPTY_DECK' });

    expect(root().textContent).toContain('Add cards before training');
    expect(root().querySelector<HTMLAnchorElement>('a.primary-button')?.getAttribute('href')).toBe(
      '/decks/1',
    );
  });

  it('shows a distinct caught-up state when every card is scheduled', async () => {
    await render({ hasCard: false, reason: 'NO_CARDS_TO_TRAIN' });

    expect(root().textContent).toContain('You’re caught up');
    expect(root().textContent).toContain('no new or due cards');
  });

  it('does not submit the same review twice if loading the next card fails', async () => {
    const nextCard = { ...card, cardId: 12, term: 'herstellen' };
    trainingService.review.mockReturnValue(of({ ...card, intervalDays: 7 }));
    trainingService.next
      .mockReturnValueOnce(throwError(() => new Error('temporary failure')))
      .mockReturnValueOnce(of({ hasCard: true, card: nextCard }));
    await render({ hasCard: true, card });

    buttonByText('Show answer').click();
    harness.detectChanges();
    ratingButton('GOOD').click();
    harness.detectChanges();

    expect(root().textContent).toContain('Your review was saved');
    expect(root().textContent).not.toContain(card.term);

    buttonByText('Try next card').click();
    harness.detectChanges();

    expect(trainingService.review).toHaveBeenCalledTimes(1);
    expect(trainingService.next).toHaveBeenCalledTimes(2);
    expect(root().textContent).toContain('herstellen');
  });

  async function render(session: TrainingNextResponse): Promise<void> {
    trainingService.start.mockReturnValue(of(session));
    await harness.navigateByUrl(`/training?deckId=${deck.id}`, TrainingPage);
    harness.detectChanges();
  }

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

  function ratingButton(rating: string): HTMLButtonElement {
    const button = root().querySelector<HTMLButtonElement>(`[data-rating="${rating}"]`);
    if (!button) {
      throw new Error(`Rating button not found: ${rating}`);
    }
    return button;
  }
});
