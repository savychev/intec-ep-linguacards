import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Deck } from '../../../core/api/api.models';
import { DeckService } from '../data-access/deck.service';
import { DecksListPage } from './decks-list.page';

describe('DecksListPage', () => {
  const dutchDeck: Deck = {
    id: 1,
    name: 'Dutch B2',
    languageCode: 'nl',
    isPrivate: true,
  };
  const frenchDeck: Deck = {
    id: 2,
    name: 'French expressions',
    languageCode: 'fr',
    isPrivate: true,
  };
  const service = {
    list: vi.fn(),
    get: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn(),
  };

  let fixture: ComponentFixture<DecksListPage>;

  beforeEach(async () => {
    service.list.mockReset().mockReturnValue(of([dutchDeck, frenchDeck]));
    service.create.mockReset();
    service.update.mockReset();
    service.delete.mockReset();

    await TestBed.configureTestingModule({
      imports: [DecksListPage],
      providers: [provideRouter([]), { provide: DeckService, useValue: service }],
    }).compileComponents();

    fixture = TestBed.createComponent(DecksListPage);
    fixture.detectChanges();
  });

  it('renders decks returned by the API', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent;

    expect(text).toContain('Dutch B2');
    expect(text).toContain('French expressions');
  });

  it('creates a trimmed private deck and adds it to the list', () => {
    const created: Deck = {
      id: 3,
      name: 'Portuguese verbs',
      languageCode: 'pt',
      isPrivate: true,
    };
    service.create.mockReturnValue(of(created));

    buttonByText('New deck').click();
    fixture.detectChanges();
    setInput('#deck-name', '  Portuguese verbs  ');
    setInput('#language-code', 'PT');
    submitEditor();

    expect(service.create).toHaveBeenCalledWith({
      name: 'Portuguese verbs',
      languageCode: 'pt',
      isPrivate: true,
    });
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Portuguese verbs');
  });

  it('updates an existing deck', () => {
    const updated = { ...dutchDeck, name: 'Dutch C1' };
    service.update.mockReturnValue(of(updated));

    buttonByText('Edit').click();
    fixture.detectChanges();
    setInput('#deck-name', 'Dutch C1');
    submitEditor();

    expect(service.update).toHaveBeenCalledWith(dutchDeck.id, {
      name: 'Dutch C1',
      languageCode: 'nl',
      isPrivate: true,
    });
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Dutch C1');
  });

  it('requires confirmation before deleting a deck', () => {
    service.delete.mockReturnValue(of(undefined));

    buttonByText('Delete').click();
    fixture.detectChanges();
    expect(service.delete).not.toHaveBeenCalled();

    buttonByText('Delete permanently').click();
    fixture.detectChanges();

    expect(service.delete).toHaveBeenCalledWith(dutchDeck.id);
    expect((fixture.nativeElement as HTMLElement).textContent).not.toContain('Dutch B2');
  });

  function buttonByText(text: string): HTMLButtonElement {
    const button = [...(fixture.nativeElement as HTMLElement).querySelectorAll('button')].find(
      (candidate) => candidate.textContent?.trim() === text,
    );
    if (!button) {
      throw new Error(`Button not found: ${text}`);
    }
    return button;
  }

  function setInput(selector: string, value: string): void {
    const input = (fixture.nativeElement as HTMLElement).querySelector<HTMLInputElement>(selector);
    if (!input) {
      throw new Error(`Input not found: ${selector}`);
    }
    input.value = value;
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  function submitEditor(): void {
    const form = (fixture.nativeElement as HTMLElement).querySelector<HTMLFormElement>('form');
    if (!form) {
      throw new Error('Deck form not found');
    }
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }
});
