import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatProgressSpinnerModule
  ],
  template: `
    <div class="auth-wrapper">
      <mat-card class="auth-card">
        <mat-card-header>
          <mat-card-title>SupportDesk Pro X</mat-card-title>
          <mat-card-subtitle>Entre na sua conta</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="submit()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>E-mail</mat-label>
              <input matInput type="email" formControlName="email" autocomplete="email">
              @if (form.get('email')?.hasError('required')) {
                <mat-error>E-mail obrigatório</mat-error>
              }
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Senha</mat-label>
              <input matInput type="password" formControlName="password" autocomplete="current-password">
              @if (form.get('password')?.hasError('required')) {
                <mat-error>Senha obrigatória</mat-error>
              }
            </mat-form-field>

            @if (error) {
              <p class="error-msg">{{ error }}</p>
            }

            <button mat-raised-button color="primary" type="submit"
                    class="full-width submit-btn"
                    [disabled]="loading || form.invalid">
              @if (loading) {
                <mat-spinner diameter="20"/>
              } @else {
                Entrar
              }
            </button>
          </form>
        </mat-card-content>

        <mat-card-actions>
          <p>Não tem conta? <a routerLink="/auth/register">Cadastre-se</a></p>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .auth-wrapper { display: flex; justify-content: center; align-items: center; height: 100vh; background: #ede7f6; }
    .auth-card { width: 100%; max-width: 420px; padding: 24px; }
    .full-width { width: 100%; }
    .submit-btn { margin-top: 16px; }
    .error-msg { color: #c62828; font-size: 0.85rem; margin-bottom: 8px; }
  `]
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authSvc: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email:    ['admin@supportdesk.com', [Validators.required, Validators.email]],
      password: ['Admin@2024!', Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.authSvc.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/']),
      error: err => {
        this.error = this.resolveLoginError(err);
        this.loading = false;
      }
    });
  }

  private resolveLoginError(err: any): string {
    if (!err) return 'Falha ao autenticar. Tente novamente.';

    if (err.status === 0) {
      return 'Servidor indisponível no momento. Verifique se o backend está ativo.';
    }

    if (err.status >= 500) {
      return 'Erro interno no servidor. Tente novamente em instantes.';
    }

    return err?.error?.message ?? 'Credenciais inválidas';
  }
}
