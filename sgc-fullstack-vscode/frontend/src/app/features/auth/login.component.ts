import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="page-center">
    <div class="card">
      <h1>WM Sistemas</h1>
      <p class="subtitle">Acesse o sistema</p>
      <form (ngSubmit)="entrar()">
        <label>E-mail</label>
        <input [(ngModel)]="email" name="email" type="email" required>
        <label>Senha</label>
        <input [(ngModel)]="senha" name="senha" type="password" required>
        <button type="submit">Entrar</button>
      </form>
      <p class="hint">Usuário padrão: admin&#64;sgc.com / 123456</p>
      <p *ngIf="erro" class="error">{{ erro }}</p>
    </div>
  </div>
  `
})
export class LoginComponent {
  email = 'admin@sgc.com';
  senha = '123456';
  erro = '';

  constructor(private authService: AuthService, private router: Router) {}

  entrar(): void {
    this.erro = '';
    this.authService.login(this.email, this.senha).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: err => this.erro = err?.error?.message || 'Falha no login'
    });
  }
}
