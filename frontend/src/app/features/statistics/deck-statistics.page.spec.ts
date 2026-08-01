import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { Deck, DeckStats } from '../../core/api/api.models';
import { DeckService } from '../decks/data-access/deck.service';
import { DeckStatisticsService } from './data-access/deck-statistics.service';
import { DeckStatisticsPage } from './deck-statistics.page';

describe('DeckStatisticsPage', () => {
  const deck: Deck = {
    id: 1,
    name: 'Dutch B2',
    languageCode: 'nl',
    isPrivate: true,
  };
  const stats: DeckStats = {
    deckId: deck.id,
    totalCards: 20,
    newCards: 5,
    dueCards: 7,
    scheduledCards: 8,
  };
  const deckService = { get: vi.fn() };
  const statisticsService = { get: vi.fn() };

  let harness: RouterTestingHarness;

  beforeEach(async () => {
    deckService.get.mockReset();
    statisticsService.get.mockReset();

    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'stats', component: DeckStatisticsPage }]),
        { provide: DeckService, useValue: deckService },
        { provide: DeckStatisticsService, useValue: statisticsService },
      ],
    });

    harness = await RouterTestingHarness.create();
  });

  it('loads the deck and renders all scheduling metrics', async () => {
    await render(stats);

    expect(deckService.get).toHaveBeenCalledWith(deck.id);
    expect(statisticsService.get).toHaveBeenCalledWith(deck.id);
    expect(root().textContent).toContain('Dutch B2');
    expect(root().textContent).toContain('Total cards');
    expect(root().textContent).toContain('Due now');
    expect(root().querySelector('.distribution-bar')?.getAttribute('aria-label')).toBe(
      '5 new, 7 due now, 8 scheduled for later.',
    );
  });

  it('links cards that are ready now to training', async () => {
    await render(stats);

    const trainingLink = [...root().querySelectorAll<HTMLAnchorElement>('a')].find(
      (candidate) => candidate.textContent?.trim() === 'Start training',
    );

    expect(root().textContent).toContain('12 cards are ready now');
    expect(trainingLink?.getAttribute('href')).toBe('/training?deckId=1');
  });

  it('shows a useful empty state for a deck without cards', async () => {
    await render({
      deckId: deck.id,
      totalCards: 0,
      newCards: 0,
      dueCards: 0,
      scheduledCards: 0,
    });

    expect(root().textContent).toContain('No cards to measure yet');
    expect(root().querySelector<HTMLAnchorElement>('a.primary-button')?.getAttribute('href')).toBe(
      '/decks/1',
    );
  });

  it('retries a failed statistics request without leaving the page', async () => {
    deckService.get.mockReturnValue(of(deck));
    statisticsService.get
      .mockReturnValueOnce(throwError(() => new Error('temporary failure')))
      .mockReturnValueOnce(of(stats));

    await harness.navigateByUrl(`/stats?deckId=${deck.id}`, DeckStatisticsPage);
    harness.detectChanges();
    expect(root().textContent).toContain('Statistics could not be loaded');

    buttonByText('Try again').click();
    harness.detectChanges();

    expect(statisticsService.get).toHaveBeenCalledTimes(2);
    expect(root().textContent).toContain('Deck statistics');
  });

  it('rejects an invalid deck query before making API requests', async () => {
    await harness.navigateByUrl('/stats?deckId=invalid', DeckStatisticsPage);
    harness.detectChanges();

    expect(deckService.get).not.toHaveBeenCalled();
    expect(statisticsService.get).not.toHaveBeenCalled();
    expect(root().textContent).toContain('missing a valid deck');
  });

  async function render(response: DeckStats): Promise<void> {
    deckService.get.mockReturnValue(of(deck));
    statisticsService.get.mockReturnValue(of(response));
    await harness.navigateByUrl(`/stats?deckId=${deck.id}`, DeckStatisticsPage);
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
});
