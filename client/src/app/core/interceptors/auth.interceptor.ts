import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const AUTH_EXCLUDED_PATHS = ['/auth/login', '/auth/register', '/auth/refresh', '/auth/logout'];

let isRefreshing = false;
let refreshSubject: BehaviorSubject<string | null> | null = null;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (AUTH_EXCLUDED_PATHS.some((path) => req.url.includes(path))) {
    return next(req);
  }

  const authService = inject(AuthService);
  const router = inject(Router);
  const accessToken = authService.getAccessToken();

  if (!accessToken) {
    return next(req);
  }

  const authedRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return next(authedRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || AUTH_EXCLUDED_PATHS.some((path) => req.url.includes(path))) {
        return throwError(() => error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        refreshSubject = new BehaviorSubject<string | null>(null);

        authService.refreshToken().subscribe({
          next: (session) => {
            isRefreshing = false;
            refreshSubject?.next(session.accessToken);
          },
          error: (refreshError: Error) => {
            isRefreshing = false;
            refreshSubject?.error(refreshError);
            refreshSubject = null;
            authService.logout();
            void router.navigate(['/auth/login']);
          },
        });
      }

      return refreshSubject!.pipe(
        filter((token): token is string => token !== null),
        take(1),
        switchMap((token) =>
          next(
            req.clone({
              setHeaders: {
                Authorization: `Bearer ${token}`,
              },
            }),
          ),
        ),
      );
    }),
  );
};