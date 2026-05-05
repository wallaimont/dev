import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '@env/environment';
import { LoginRequest, LoginResponse } from '../models/api.models';

const TOKEN_KEY = 'pr_assistant_token';
const USER_KEY = 'pr_assistant_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly username = signal<string | null>(localStorage.getItem(USER_KEY));

  constructor(private http: HttpClient, private router: Router) {}

  get token(): string | null {
    return this.tokenSignal();
  }

  login(req: LoginRequest) {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, req).pipe(
      tap(res => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, res.username);
        this.tokenSignal.set(res.token);
        this.username.set(res.username);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.tokenSignal.set(null);
    this.username.set(null);
    this.router.navigate(['/login']);
  }
}
