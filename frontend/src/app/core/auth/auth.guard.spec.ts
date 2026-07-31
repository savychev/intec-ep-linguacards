import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { AuthService } from './auth.service';
import { authGuard, guestGuard } from './auth.guard';

describe('authentication guards', () => {
  const loginTree = {} as UrlTree;
  const decksTree = {} as UrlTree;
  const auth = { getValidAccessToken: vi.fn<() => string | null>() };
  const router = {
    createUrlTree: vi.fn((commands: string[]) =>
      commands[0] === '/login' ? loginTree : decksTree,
    ),
  };

  beforeEach(() => {
    auth.getValidAccessToken.mockReset();
    router.createUrlTree.mockClear();
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });
  });

  it('redirects a guest to login and preserves the requested URL', () => {
    auth.getValidAccessToken.mockReturnValue(null);

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, { url: '/decks' } as RouterStateSnapshot),
    );

    expect(result).toBe(loginTree);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/login'], {
      queryParams: { returnUrl: '/decks' },
    });
  });

  it('allows a user with a valid token into protected routes', () => {
    auth.getValidAccessToken.mockReturnValue('token');

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, { url: '/decks' } as RouterStateSnapshot),
    );

    expect(result).toBe(true);
  });

  it('redirects an authenticated user away from guest-only pages', () => {
    auth.getValidAccessToken.mockReturnValue('token');

    const result = TestBed.runInInjectionContext(() =>
      guestGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );

    expect(result).toBe(decksTree);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/decks']);
  });
});
