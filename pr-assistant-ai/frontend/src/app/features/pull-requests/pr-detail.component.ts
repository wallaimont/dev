import { Component, Input, OnInit, signal, ChangeDetectionStrategy } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { PullRequestResponse, FinalAnalysisResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-pr-detail',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe, NgClass, RouterLink, MatCardModule, MatChipsModule, MatIconModule, MatDividerModule, MatTableModule, MatProgressSpinnerModule, MatButtonModule, MatTooltipModule],
  template: `
    @if (loading()) {
      <div class="center"><mat-spinner></mat-spinner></div>
    } @else {
    @if (pr(); as p) {
      <div class="header-row">
        <button mat-button routerLink="/pull-requests"><mat-icon>arrow_back</mat-icon> Voltar</button>
        <h2>#{{ p.externalPrId }} — {{ p.title }}</h2>
      </div>

      <div class="meta-grid">
        <mat-card>
          <div class="meta-item"><strong>Repositório</strong><span>{{ p.repositoryOwner }}/{{ p.repositoryName }}</span></div>
          <div class="meta-item"><strong>Autor</strong><span>{{ p.author }}</span></div>
          <div class="meta-item"><strong>Branch</strong><span>{{ p.sourceBranch }} → {{ p.targetBranch }}</span></div>
          <div class="meta-item"><strong>Estado</strong><mat-chip>{{ p.state }}</mat-chip></div>
          <div class="meta-item"><strong>Criado</strong><span>{{ p.createdAt | date:'dd/MM/yyyy HH:mm' }}</span></div>
        </mat-card>

        @if (p.latestFinalAnalysis) {
          <mat-card class="analysis-card">
            <h3>Análise Final</h3>
            <div class="analysis-score" [ngClass]="'risk-' + p.latestFinalAnalysis.riskLevel.toLowerCase()">
              <span class="big-score">{{ p.latestFinalAnalysis.finalScore }}</span>/100
            </div>
            <div class="risk-badge" [ngClass]="'risk-' + p.latestFinalAnalysis.riskLevel.toLowerCase()">{{ p.latestFinalAnalysis.riskLevel }}</div>
            <p class="decision">{{ p.latestFinalAnalysis.decisionReason }}</p>
          </mat-card>
        }
      </div>

      @if (p.description) {
        <mat-card class="section-card">
          <h3>Descrição</h3>
          <p>{{ p.description }}</p>
        </mat-card>
      }

      <mat-card class="section-card">
        <h3>Arquivos ({{ p.files.length }})</h3>
        <table mat-table [dataSource]="p.files" class="files-table">
          <ng-container matColumnDef="filePath">
            <th mat-header-cell *matHeaderCellDef>Arquivo</th>
            <td mat-cell *matCellDef="let f">
              <span [ngClass]="{ 'critical-file': f.critical }">{{ f.filePath }}</span>
              @if (f.critical) { <mat-icon class="critical-icon" matTooltip="{{ f.criticalReason }}">warning</mat-icon> }
            </td>
          </ng-container>
          <ng-container matColumnDef="fileStatus">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let f">{{ f.fileStatus }}</td>
          </ng-container>
          <ng-container matColumnDef="changes">
            <th mat-header-cell *matHeaderCellDef>Alterações</th>
            <td mat-cell *matCellDef="let f">
              <span class="additions">+{{ f.additions }}</span> /
              <span class="deletions">-{{ f.deletions }}</span>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="fileColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: fileColumns;"></tr>
        </table>
      </mat-card>

      @if (analyses().length) {
        <mat-card class="section-card">
          <h3>Histórico de Análises</h3>
          @for (a of analyses(); track a.id) {
            <div class="history-item">
              <span [ngClass]="'risk-' + a.riskLevel.toLowerCase()">{{ a.riskLevel }}</span>
              <span>Score: {{ a.finalScore }}</span>
              <span class="date">{{ a.analyzedAt | date:'dd/MM/yy HH:mm' }}</span>
              <p>{{ a.decisionReason }}</p>
            </div>
            <mat-divider />
          }
        </mat-card>
      }
    }
    }
  `,
  styles: [`
    .center { display: flex; justify-content: center; padding: 80px 0; }
    .header-row { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
    .header-row h2 { margin: 0; font-weight: 600; }
    .meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
    .meta-item { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid rgba(255,255,255,0.06); }
    .analysis-card { text-align: center; padding: 24px; }
    .analysis-card h3 { margin: 0 0 12px; }
    .big-score { font-size: 48px; font-weight: 700; }
    .risk-badge { display: inline-block; padding: 4px 16px; border-radius: 16px; margin: 12px 0; font-weight: 600; }
    .decision { opacity: 0.8; font-size: 14px; }
    .section-card { margin-bottom: 16px; padding: 16px; }
    .section-card h3 { margin: 0 0 12px; font-weight: 600; }
    .files-table { width: 100%; }
    .critical-file { color: #ff4444; }
    .critical-icon { font-size: 16px; width: 16px; height: 16px; color: #ff4444; vertical-align: middle; margin-left: 4px; }
    .additions { color: #44cc44; }
    .deletions { color: #ff4444; }
    .history-item { padding: 12px 0; display: flex; flex-wrap: wrap; gap: 16px; align-items: baseline; }
    .history-item p { width: 100%; margin: 4px 0 0; opacity: 0.7; font-size: 14px; }
    .date { opacity: 0.5; font-size: 13px; }
  `]
})
export class PrDetailComponent implements OnInit {
  @Input() id!: string;

  loading = signal(true);
  pr = signal<PullRequestResponse | null>(null);
  analyses = signal<FinalAnalysisResponse[]>([]);
  fileColumns = ['filePath', 'fileStatus', 'changes'];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getPullRequest(this.id).subscribe(pr => {
      this.pr.set(pr);
      this.loading.set(false);
    });
    this.api.getAnalyses(this.id).subscribe(a => this.analyses.set(a));
  }
}
