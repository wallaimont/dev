import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="layout">
      <aside class="sidebar">
        <h2>WM Sistemas</h2>
        <a routerLink="/dashboard">Dashboard</a>
        <a routerLink="/clientes">Clientes</a>
        <button (click)="sair()">Sair</button>
      </aside>
      <main class="content">
        <h1>Dashboard</h1>
        <div class="grid">
          <div class="stat-card"><strong>Clientes</strong><span>Módulo ativo</span></div>
          <div class="stat-card"><strong>Produtos</strong><span>Estrutura pronta</span></div>
          <div class="stat-card"><strong>Pedidos</strong><span>Expansão futura</span></div>
        </div>
        <p>Projeto base pronto para rodar no VS Code com Angular + Spring Boot + PostgreSQL.</p>
      </main>
    </div>
  `
})
export class DashboardComponent {
  constructor(private auth: AuthService, private router: Router) {}

  sair(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
