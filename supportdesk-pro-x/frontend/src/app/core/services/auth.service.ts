import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, UserSummary } from '../models/auth.model';
import { ApiResponse } from '../models/api.model';

const TOKEN_KEY   = 'sd_access_token';
const REFRESH_KEY = 'sd_refresh_token';
const USER_KEY    = 'sd_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = `${environment.apiUrl}/auth`;
  private _currentUser = new BehaviorSubject<UserSummary | null>(this.loadUser());

  currentUser$ = this._currentUser.asObservable();

  constructor(private http: HttpClient) {}

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.base}/login`, req).pipe(
      map(r => r.data),
      tap(r => this.storeSession(r))
    );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.base}/register`, req).pipe(
      map(r => r.data),
      tap(r => this.storeSession(r))
    );
  }

  refresh(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(REFRESH_KEY) ?? '';
    return this.http.post<ApiResponse<AuthResponse>>(`${this.base}/refresh`, { refreshToken }).pipe(
      map(r => r.data),
      tap(r => this.storeSession(r))
    );
  }

  logout(): void {
    this.http.post(`${this.base}/logout`, {}).subscribe({
      complete: () => this.clearSession(),
      error: () => this.clearSession()
    });
  }

  getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  get currentUser(): UserSummary | null {
    return this._currentUser.value;
  }

  private storeSession(r: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, r.accessToken);
    localStorage.setItem(REFRESH_KEY, r.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(r.user));
    this._currentUser.next(r.user);
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this._currentUser.next(null);
  }

  private loadUser(): UserSummary | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
