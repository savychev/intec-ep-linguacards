import { HttpErrorResponse } from '@angular/common/http';

import { ApiErrorResponse } from './api.models';

export function apiErrorMessage(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }

  if (error.status === 0) {
    return 'The server is unavailable. Check that the backend is running and try again.';
  }

  const response = error.error as Partial<ApiErrorResponse> | null;
  if (response && typeof response.message === 'string' && response.message.trim()) {
    return response.message;
  }

  return fallback;
}
