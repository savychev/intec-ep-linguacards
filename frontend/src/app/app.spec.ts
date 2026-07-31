import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { App } from './app';
import { AuthService } from './core/auth/auth.service';

describe('App', () => {
  const authenticated = signal(false);
  const auth = {
    isAuthenticated: authenticated.asReadonly(),
    logout: vi.fn(),
  };

  beforeEach(async () => {
    authenticated.set(false);
    auth.logout.mockReset();

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    }).compileComponents();
  });

  it('renders the application brand', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('.brand')?.textContent).toContain(
      'LinguaCards',
    );
  });

  it('offers sign out for an authenticated user', () => {
    authenticated.set(true);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const button = (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>(
      'button',
    );
    button?.click();

    expect(auth.logout).toHaveBeenCalledOnce();
  });
});
