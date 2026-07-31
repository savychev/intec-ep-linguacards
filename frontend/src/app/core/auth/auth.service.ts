import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { map, Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthCredentials, AuthResponse } from '../api/api.models';

const ACCESS_TOKEN_KEY = 'linguacards.accessToken';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly tokenState = signal<string | null>(this.loadToken());

  readonly accessToken = this.tokenState.asReadonly();
  readonly isAuthenticated = computed(() => this.tokenState() !== null);

  register(credentials: AuthCredentials): Observable<void> {
    return this.http
      .post<AuthResponse>(`${environment.apiBaseUrl}/api/auth/register`, credentials)
      .pipe(map(() => undefined));
  }

  login(credentials: AuthCredentials): Observable<void> {
    return this.http
      .post<AuthResponse>(`${environment.apiBaseUrl}/api/auth/login`, credentials)
      .pipe(
        map((response) => this.requireAccessToken(response)),
        tap((token) => this.storeToken(token)),
        map(() => undefined),
      );
  }

  getValidAccessToken(): string | null {
    const token = this.tokenState();
    if (!token || !this.isTokenValid(token)) {
      this.clearSession();
      return null;
    }

    return token;
  }

  clearSession(): void {
    this.tokenState.set(null);
    this.storage?.removeItem(ACCESS_TOKEN_KEY);
  }

  logout(): void {
    this.clearSession();
    void this.router.navigateByUrl('/login');
  }

  private get storage(): Storage | null {
    return isPlatformBrowser(this.platformId) ? window.localStorage : null;
  }

  private loadToken(): string | null {
    const token = this.storage?.getItem(ACCESS_TOKEN_KEY) ?? null;
    if (token && this.isTokenValid(token)) {
      return token;
    }

    this.storage?.removeItem(ACCESS_TOKEN_KEY);
    return null;
  }

  private storeToken(token: string): void {
    this.storage?.setItem(ACCESS_TOKEN_KEY, token);
    this.tokenState.set(token);
  }

  private requireAccessToken(response: AuthResponse): string {
    const token = response.accessToken?.trim();
    if (!token) {
      throw new Error('The login response did not contain an access token.');
    }

    if (!this.isTokenValid(token)) {
      throw new Error('The login response contained an invalid access token.');
    }

    return token;
  }

  private isTokenValid(token: string): boolean {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        return false;
      }

      const payload = JSON.parse(this.decodeBase64Url(parts[1])) as { exp?: unknown };
      return typeof payload.exp === 'number' && payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  private decodeBase64Url(value: string): string {
    const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
    const padding = '='.repeat((4 - (normalized.length % 4)) % 4);
    return atob(normalized + padding);
  }
}
