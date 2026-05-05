import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TicketService } from '../../../core/services/ticket.service';
import { TicketResponse } from '../../../core/models/ticket.model';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    MatTableModule, MatChipsModule, MatButtonModule,
    MatIconModule, MatPaginatorModule, MatProgressBarModule
  ],
  template: `
    <div class="header-row">
      <h2>Chamados</h2>
      <a mat-raised-button color="primary" routerLink="/tickets/new">
        <mat-icon>add</mat-icon> Novo chamado
      </a>
    </div>

    @if (loading) { <mat-progress-bar mode="indeterminate"/> }

    <table mat-table [dataSource]="tickets" class="mat-elevation-z2 full-width">
      <ng-container matColumnDef="ticketNumber">
        <th mat-header-cell *matHeaderCellDef>Nº</th>
        <td mat-cell *matCellDef="let t">{{ t.ticketNumber }}</td>
      </ng-container>

      <ng-container matColumnDef="title">
        <th mat-header-cell *matHeaderCellDef>Título</th>
        <td mat-cell *matCellDef="let t">
          <a [routerLink]="['/tickets', t.id]">{{ t.title }}</a>
        </td>
      </ng-container>

      <ng-container matColumnDef="status">
        <th mat-header-cell *matHeaderCellDef>Status</th>
        <td mat-cell *matCellDef="let t">
          <span [class]="'status-chip ' + t.status">{{ t.status }}</span>
        </td>
      </ng-container>

      <ng-container matColumnDef="priority">
        <th mat-header-cell *matHeaderCellDef>Prioridade</th>
        <td mat-cell *matCellDef="let t">
          <span [class]="'priority-chip ' + t.priority">{{ t.priority }}</span>
        </td>
      </ng-container>

      <ng-container matColumnDef="category">
        <th mat-header-cell *matHeaderCellDef>Categoria</th>
        <td mat-cell *matCellDef="let t">{{ t.category.name }}</td>
      </ng-container>

      <ng-container matColumnDef="createdAt">
        <th mat-header-cell *matHeaderCellDef>Criado em</th>
        <td mat-cell *matCellDef="let t">{{ t.createdAt | date:'dd/MM/yy HH:mm' }}</td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="columns"></tr>
      <tr mat-row *matRowDef="let row; columns: columns;"></tr>
    </table>

    <mat-paginator
      [length]="totalElements"
      [pageSize]="pageSize"
      [pageSizeOptions]="[10, 20, 50]"
      (page)="onPage($event)"
      showFirstLastButtons/>
  `,
  styles: [`
    .header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .full-width { width: 100%; }
    table { margin-bottom: 0; }
  `]
})
export class TicketListComponent implements OnInit {
  columns = ['ticketNumber', 'title', 'status', 'priority', 'category', 'createdAt'];
  tickets: TicketResponse[] = [];
  totalElements = 0;
  pageSize = 20;
  loading = false;

  constructor(private ticketSvc: TicketService) {}

  ngOnInit(): void { this.load(0); }

  load(page: number): void {
    this.loading = true;
    this.ticketSvc.list(page, this.pageSize).subscribe({
      next: r => {
        this.tickets = r.content;
        this.totalElements = r.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onPage(e: PageEvent): void {
    this.pageSize = e.pageSize;
    this.load(e.pageIndex);
  }
}
