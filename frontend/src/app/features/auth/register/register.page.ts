import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { apiErrorMessage } from '../../../core/api/api-error';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.page.html',
  styleUrl: '../auth-page.css',
})
export class RegisterPage {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email, Validators.maxLength(320)]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(72)]],
  });

  protected submit(): void {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null);
    this.submitting.set(true);
    this.form.disable();

    this.auth
      .register(this.form.getRawValue())
      .pipe(
        finalize(() => {
          this.submitting.set(false);
          this.form.enable();
        }),
      )
      .subscribe({
        next: () => void this.router.navigate(['/login'], { queryParams: { registered: 'true' } }),
        error: (error: unknown) =>
          this.errorMessage.set(
            apiErrorMessage(error, 'Unable to create your account. Please try again.'),
          ),
      });
  }
}
