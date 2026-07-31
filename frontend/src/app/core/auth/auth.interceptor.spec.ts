import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { environment } from '../../../environments/environment';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  const router = {
    url: '/decks',
    navigate: vi.fn().mockResolvedValue(true),
    navigateByUrl: vi.fn().mockResolvedValue(true),
  };

  beforeEach(() => {
    localStorage.clear();
    router.navigate.mockClear();
    localStorage.setItem('linguacards.accessToken', createToken(Date.now() + 60_000));

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
      ],
    });
  });

  afterEach(() => {
    TestBed.inject(HttpTestingController).verify();
    localStorage.clear();
  });

  it('adds a bearer token to protected API requests', () => {
    TestBed.inject(HttpClient).get(`${environment.apiBaseUrl}/api/decks`).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${environment.apiBaseUrl}/api/decks`,
    );
    expect(request.request.headers.get('Authorization')).toMatch(/^Bearer /);
    request.flush([]);
  });

  it('does not send the token to public authentication endpoints', () => {
    TestBed.inject(HttpClient)
      .post(`${environment.apiBaseUrl}/api/auth/login`, {
        email: 'learner@example.com',
        password: 'secret12',
      })
      .subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${environment.apiBaseUrl}/api/auth/login`,
    );
    expect(request.request.headers.has('Authorization')).toBe(false);
    request.flush({ message: 'ok' });
  });

  it('clears the session and redirects when an authenticated request receives 401', () => {
    TestBed.inject(HttpClient)
      .get(`${environment.apiBaseUrl}/api/decks`)
      .subscribe({
        error: () => undefined,
      });

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${environment.apiBaseUrl}/api/decks`,
    );
    request.flush(
      { message: 'Authentication is required' },
      { status: 401, statusText: 'Unauthorized' },
    );

    expect(localStorage.getItem('linguacards.accessToken')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { returnUrl: '/decks', sessionExpired: 'true' },
    });
  });
});

function createToken(expiration: number): string {
  const payload = btoa(JSON.stringify({ exp: Math.floor(expiration / 1000) }))
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');
  return `header.${payload}.signature`;
}
