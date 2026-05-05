import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ClienteService } from '../../core/services/cliente.service';
import { Cliente } from '../../shared/models/cliente.model';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
  <div class="layout">
    <aside class="sidebar">
      <h2>WM Sistemas</h2>
      <a routerLink="/dashboard">Dashboard</a>
      <a routerLink="/clientes">Clientes</a>
    </aside>
    <main class="content">
      <h1>Clientes</h1>
      <div class="card form-card">
        <h3>Novo cliente</h3>
        <div class="form-grid">
          <input [(ngModel)]="cliente.nome" placeholder="Nome">
          <input [(ngModel)]="cliente.email" placeholder="E-mail">
          <input [(ngModel)]="cliente.telefone" placeholder="Telefone">
          <input [(ngModel)]="cliente.documento" placeholder="Documento">
          <input [(ngModel)]="cliente.endereco" placeholder="Endereço">
        </div>
        <button (click)="salvar()">Salvar</button>
      </div>

      <div class="card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>E-mail</th>
              <th>Telefone</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of clientes">
              <td>{{ item.id }}</td>
              <td>{{ item.nome }}</td>
              <td>{{ item.email }}</td>
              <td>{{ item.telefone }}</td>
              <td><button (click)="excluir(item.id!)">Excluir</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>
  </div>
  `
})
export class ClientesComponent implements OnInit {
  clientes: Cliente[] = [];
  cliente: Cliente = { nome: '', email: '', telefone: '', documento: '', endereco: '' };

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.clienteService.listar().subscribe({
      next: data => this.clientes = data,
      error: err => console.error(err)
    });
  }

  salvar(): void {
    if (!this.cliente.nome) return;
    this.clienteService.criar(this.cliente).subscribe({
      next: () => {
        this.cliente = { nome: '', email: '', telefone: '', documento: '', endereco: '' };
        this.carregar();
      },
      error: err => alert(err?.error?.message || 'Erro ao salvar cliente')
    });
  }

  excluir(id: number): void {
    this.clienteService.excluir(id).subscribe(() => this.carregar());
  }
}
