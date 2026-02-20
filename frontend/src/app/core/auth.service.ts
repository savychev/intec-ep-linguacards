import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly emailKey = 'linguacards_email';
  private readonly passwordKey = 'linguacards_password';

  readonly email = signal<string | null>(localStorage.getItem(this.emailKey));
  readonly password = signal<string | null>(localStorage.getItem(this.passwordKey));
  readonly isLoggedIn = computed(() => !!this.email() && !!this.password());

  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiBaseUrl}/auth/login`, { email, password }).pipe(
      tap(() => this.storeSession(email, password))
    );
  }

  logout(): void {
    localStorage.removeItem(this.emailKey);
    localStorage.removeItem(this.passwordKey);
    this.email.set(null);
    this.password.set(null);
  }

  getBasicAuthHeader(): string | null {
    const email = this.email();
    const password = this.password();

    if (!email || !password) {
      return null;
    }

    return `Basic ${btoa(`${email}:${password}`)}`;
  }

  private storeSession(email: string, password: string): void {
    localStorage.setItem(this.emailKey, email);
    localStorage.setItem(this.passwordKey, password);
    this.email.set(email);
    this.password.set(password);
  }
}
