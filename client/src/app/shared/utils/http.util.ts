import { HttpErrorResponse, HttpParams } from '@angular/common/http';

export function buildHttpParams(
  params: Record<string, string | number | boolean | null | undefined>,
): HttpParams {
  let httpParams = new HttpParams();

  for (const [key, value] of Object.entries(params)) {
    if (value != null && value !== '') {
      httpParams = httpParams.set(key, value.toString());
    }
  }

  return httpParams;
}

export function getErrorMessage(
  error: HttpErrorResponse,
  fallback: string,
): string {
  return typeof error.error?.message === 'string' ? error.error.message : fallback;
}
