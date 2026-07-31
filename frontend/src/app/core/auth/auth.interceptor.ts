import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getValidAccessToken();
  const apiRoot = `${environment.apiBaseUrl.replace(/\/$/, '')}/api`;
  const isApiRequest = request.url === apiRoot || request.url.startsWith(`${apiRoot}/`);
  const isPublicAuthRequest =
    request.url === `${apiRoot}/auth/login` || request.url === `${apiRoot}/auth/register`;
  const authenticatedRequest = isApiRequest && !isPublicAuthRequest && token !== null;

  const outgoingRequest = authenticatedRequest
    ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : request;

  return next(outgoingRequest).pipe(
    catchError((error: unknown) => {
      if (authenticatedRequest && error instanceof HttpErrorResponse && error.status === 401) {
        const returnUrl = router.url.startsWith('/') ? router.url : '/decks';
        auth.clearSession();
        void router.navigate(['/login'], {
          queryParams: { returnUrl, sessionExpired: 'true' },
        });
      }

      return throwError(() => error);
    }),
  );
};
