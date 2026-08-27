import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/models/auth.model';

interface RefreshTokenPayload {
  refreshToken: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly authApiUrl = `${environment.apiUrl}/auth`;
  private readonly storageKey = 'jobconnect.auth.session';
  private readonly session = signal<AuthResponse | null>(this.readStoredSession());

  readonly authSession = computed(() => this.session());
  readonly isAuthenticated = computed(() => Boolean(this.session()?.accessToken));

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.authApiUrl}/login`, payload)
      .pipe(tap((response) => this.setSession(response)));
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.authApiUrl}/register`, payload)
      .pipe(tap((response) => this.setSession(response)));
  }

  refreshToken(refreshToken = this.getRefreshToken()): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.authApiUrl}/refresh`, { refreshToken } satisfies RefreshTokenPayload)
      .pipe(tap((response) => this.setSession(response)));
  }

  logout(): void {
    this.session.set(null);
    this.clearStoredSession();
  }

  getAccessToken(): string {
    return this.session()?.accessToken ?? '';
  }

  getRefreshToken(): string {
    return this.session()?.refreshToken ?? '';
  }

  private setSession(session: AuthResponse): void {
    this.session.set(session);
    this.storeSession(session);
  }

  private readStoredSession(): AuthResponse | null {
    if (!this.isStorageAvailable()) {
      return null;
    }

    const storedSession = localStorage.getItem(this.storageKey);
    return storedSession ? (JSON.parse(storedSession) as AuthResponse) : null;
  }

  private storeSession(session: AuthResponse): void {
    if (!this.isStorageAvailable()) {
      return;
    }

    localStorage.setItem(this.storageKey, JSON.stringify(session));
  }

  private clearStoredSession(): void {
    if (!this.isStorageAvailable()) {
      return;
    }

    localStorage.removeItem(this.storageKey);
  }

  private isStorageAvailable(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }
}
