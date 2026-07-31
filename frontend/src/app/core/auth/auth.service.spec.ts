import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
  });

  afterEach(() => {
    TestBed.inject(HttpTestingController).verify();
    localStorage.clear();
  });

  it('registers with the backend without creating a local session', () => {
    const service = TestBed.inject(AuthService);
    let completed = false;

    service.register({ email: 'learner@example.com', password: 'secret12' }).subscribe(() => {
      completed = true;
    });

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${environment.apiBaseUrl}/api/auth/register`,
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      email: 'learner@example.com',
      password: 'secret12',
    });

    request.flush({ message: 'registered' }, { status: 201, statusText: 'Created' });

    expect(completed).toBe(true);
    expect(service.isAuthenticated()).toBe(false);
  });

  it('stores a valid access token after login', () => {
    const service = TestBed.inject(AuthService);
    const token = createToken(Date.now() + 60_000);

    service.login({ email: 'learner@example.com', password: 'secret12' }).subscribe();

    const request = TestBed.inject(HttpTestingController).expectOne(
      `${environment.apiBaseUrl}/api/auth/login`,
    );
    expect(request.request.method).toBe('POST');
    request.flush({ message: 'ok', accessToken: token, tokenType: 'Bearer' });

    expect(service.accessToken()).toBe(token);
    expect(service.isAuthenticated()).toBe(true);
    expect(localStorage.getItem('linguacards.accessToken')).toBe(token);
  });

  it('discards an expired token restored from storage', () => {
    localStorage.setItem('linguacards.accessToken', createToken(Date.now() - 60_000));

    const service = TestBed.inject(AuthService);

    expect(service.getValidAccessToken()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
    expect(localStorage.getItem('linguacards.accessToken')).toBeNull();
  });
});

function createToken(expiration: number): string {
  const payload = base64Url(JSON.stringify({ exp: Math.floor(expiration / 1000) }));
  return `header.${payload}.signature`;
}

function base64Url(value: string): string {
  return btoa(value).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}
