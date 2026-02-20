import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-auth-page',
  imports: [ReactiveFormsModule],
  templateUrl: './auth-page.component.html',
  styleUrl: './auth-page.component.scss'
})
export class AuthPageComponent {
  mode: 'login' | 'register' = 'login';
  error = '';

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  switchMode(mode: 'login' | 'register'): void {
    this.mode = mode;
    this.error = '';
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.getRawValue();
    const request$ = this.mode === 'login'
      ? this.authService.login(email, password)
      : this.authService.register(email, password);

    request$.subscribe({
      next: () => {
        this.error = '';
        this.router.navigate(['/decks']);
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Authentication failed.';
      }
    });
  }
}
