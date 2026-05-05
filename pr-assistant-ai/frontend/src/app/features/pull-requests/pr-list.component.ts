import { Component, OnInit, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ApiService } from '../../core/services/api.service';
import { PullRequestResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-pr-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, DatePipe, MatTableModule, MatPaginatorModule, MatChipsModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <h2>Pull Requests</h2>
    @if (loading()) {
      <div class="center"><mat-spinner></mat-spinner></div>
    } @else {
      <table mat-table [dataSource]="items()" class="pr-table">
        <ng-container matColumnDef="externalPrId">
          <th mat-header-cell *matHeaderCellDef>#</th>
          <td mat-cell *matCellDef="let pr">{{ pr.externalPrId }}</td>
        </ng-container>

        <ng-container matColumnDef="title">
          <th mat-header-cell *matHeaderCellDef>Título</th>
          <td mat-cell *matCellDef="let pr">
            <a [routerLink]="['/pull-requests', pr.id]">{{ pr.title }}</a>
          </td>
        </ng-container>

        <ng-container matColumnDef="repository">
          <th mat-header-cell *matHeaderCellDef>Repositório</th>
          <td mat-cell *matCellDef="let pr">{{ pr.repositoryOwner }}/{{ pr.repositoryName }}</td>
        </ng-container>

        <ng-container matColumnDef="state">
          <th mat-header-cell *matHeaderCellDef>Estado</th>
          <td mat-cell *matCellDef="let pr">
            <mat-chip [class]="'state-' + pr.state.toLowerCase()">{{ pr.state }}</mat-chip>
          </td>
        </ng-container>

        <ng-container matColumnDef="risk">
          <th mat-header-cell *matHeaderCellDef>Risco</th>
          <td mat-cell *matCellDef="let pr">
            @if (pr.latestFinalAnalysis) {
              <span [class]="'risk-' + pr.latestFinalAnalysis.riskLevel.toLowerCase()">
                {{ pr.latestFinalAnalysis.riskLevel }}
              </span>
            } @else {
              <span class="no-analysis">—</span>
            }
          </td>
        </ng-container>

        <ng-container matColumnDef="score">
          <th mat-header-cell *matHeaderCellDef>Score</th>
          <td mat-cell *matCellDef="let pr">
            {{ pr.latestFinalAnalysis?.finalScore ?? '—' }}
          </td>
        </ng-container>

        <ng-container matColumnDef="createdAt">
          <th mat-header-cell *matHeaderCellDef>Criado em</th>
          <td mat-cell *matCellDef="let pr">{{ pr.createdAt | date:'dd/MM/yy HH:mm' }}</td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="columns"></tr>
        <tr mat-row *matRowDef="let row; columns: columns;" class="clickable"></tr>
      </table>

      <mat-paginator [length]="totalElements()" [pageSize]="20" [pageSizeOptions]="[10,20,50]"
                     (page)="onPage($event)" showFirstLastButtons></mat-paginator>
    }
  `,
  styles: [`
    h2 { margin: 0 0 16px; font-weight: 600; }
    .center { display: flex; justify-content: center; padding: 80px 0; }
    .pr-table { width: 100%; }
    .clickable { cursor: pointer; }
    .clickable:hover { background: rgba(124,139,255,0.08); }
    .no-analysis { opacity: 0.4; }
    .state-open { --mdc-chip-label-text-color: #44cc44; }
    .state-merged { --mdc-chip-label-text-color: #a855f7; }
    .state-closed { --mdc-chip-label-text-color: #ff4444; }
  `]
})
export class PrListComponent implements OnInit {
  columns = ['externalPrId', 'title', 'repository', 'state', 'risk', 'score', 'createdAt'];
  items = signal<PullRequestResponse[]>([]);
  totalElements = signal(0);
  loading = signal(true);
  private page = 0;

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  onPage(e: PageEvent): void {
    this.page = e.pageIndex;
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.api.getPullRequests(this.page).subscribe(p => {
      this.items.set(p.content);
      this.totalElements.set(p.totalElements);
      this.loading.set(false);
    });
  }
}
