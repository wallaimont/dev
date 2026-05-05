import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="login-wrapper">
      <mat-card class="login-card">
        <mat-card-header>
          <mat-icon mat-card-avatar class="header-icon">smart_toy</mat-icon>
          <mat-card-title>PR Assistant AI</mat-card-title>
          <mat-card-subtitle>Faça login para continuar</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          @if (error()) {
            <div class="error-msg">{{ error() }}</div>
          }
          <form (ngSubmit)="onLogin()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Usuário</mat-label>
              <input matInput [(ngModel)]="username" name="username" required autocomplete="username">
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Senha</mat-label>
              <input matInput [(ngModel)]="password" name="password" type="password" required autocomplete="current-password">
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit" class="full-width" [disabled]="loading()">
              @if (loading()) { <mat-spinner diameter="20"></mat-spinner> }
              @else { Entrar }
            </button>
          </form>

          <div class="quick-login">
            <p class="quick-login-label">Acesso rápido (dev):</p>
            <div class="quick-login-buttons">
              <button mat-stroked-button color="accent" (click)="fillCredentials('admin@prassistant.dev', 'admin123')">
                <mat-icon>admin_panel_settings</mat-icon> Admin
              </button>
              <button mat-stroked-button (click)="fillCredentials('reviewer@prassistant.dev', 'reviewer123')">
                <mat-icon>rate_review</mat-icon> Reviewer
              </button>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .login-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #0f0f23; }
    .login-card { width: 380px; padding: 24px; }
    .header-icon { font-size: 40px; width: 40px; height: 40px; color: #7c8bff; }
    .full-width { width: 100%; }
    .error-msg { color: #ff4444; margin-bottom: 12px; font-size: 14px; }
    form { display: flex; flex-direction: column; gap: 8px; margin-top: 16px; }
    .quick-login { margin-top: 20px; border-top: 1px solid rgba(255,255,255,0.12); padding-top: 16px; }
    .quick-login-label { font-size: 12px; color: rgba(255,255,255,0.5); margin: 0 0 8px; }
    .quick-login-buttons { display: flex; gap: 8px; }
    .quick-login-buttons button { flex: 1; }
  `]
})
export class LoginComponent {
  username = '';
  password = '';
  loading = signal(false);
  error = signal('');

  constructor(private auth: AuthService, private router: Router) {}

  fillCredentials(email: string, pass: string): void {
    this.username = email;
    this.password = pass;
  }

  onLogin(): void {
    this.loading.set(true);
    this.error.set('');
    this.auth.login({ email: this.username, password: this.password }).subscribe({
      next: () => { this.router.navigate(['/dashboard']); },
      error: () => { this.error.set('Credenciais inválidas'); this.loading.set(false); }
    });
  }
}
