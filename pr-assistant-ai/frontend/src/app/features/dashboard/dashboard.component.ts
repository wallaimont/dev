import { Component, OnInit, signal, ChangeDetectionStrategy } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { ApiService } from '../../core/services/api.service';
import { DashboardResponse } from '../../core/models/api.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DecimalPipe, MatCardModule, MatIconModule, MatProgressSpinnerModule, NgChartsModule],
  template: `
    @if (loading()) {
      <div class="center"><mat-spinner></mat-spinner></div>
    } @else {
    @if (data(); as d) {
      <h2>Dashboard</h2>
      <div class="cards-grid">
        <mat-card class="stat-card">
          <mat-icon>merge_type</mat-icon>
          <div class="stat-value">{{ d.totalPrs }}</div>
          <div class="stat-label">Total PRs</div>
        </mat-card>
        <mat-card class="stat-card">
          <mat-icon>pending</mat-icon>
          <div class="stat-value">{{ d.openPrs }}</div>
          <div class="stat-label">Abertos</div>
        </mat-card>
        <mat-card class="stat-card">
          <mat-icon>check_circle</mat-icon>
          <div class="stat-value">{{ d.approvedPrs }}</div>
          <div class="stat-label">Aprovados</div>
        </mat-card>
        <mat-card class="stat-card critical">
          <mat-icon>warning</mat-icon>
          <div class="stat-value">{{ d.criticalPrs }}</div>
          <div class="stat-label">Críticos</div>
        </mat-card>
        <mat-card class="stat-card score">
          <mat-icon>speed</mat-icon>
          <div class="stat-value">{{ d.avgQualityScore | number:'1.0-0' }}%</div>
          <div class="stat-label">Score Médio</div>
        </mat-card>
      </div>

      <div class="chart-row">
        <mat-card class="chart-card">
          <mat-card-header><mat-card-title>Distribuição de PRs</mat-card-title></mat-card-header>
          <mat-card-content>
            <canvas baseChart [data]="doughnutData" type="doughnut" [options]="chartOptions"></canvas>
          </mat-card-content>
        </mat-card>
      </div>
    }
    }
  `,
  styles: [`
    h2 { margin: 0 0 20px; font-weight: 600; }
    .center { display: flex; justify-content: center; padding: 80px 0; }
    .cards-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 24px; }
    .stat-card { text-align: center; padding: 20px; }
    .stat-card mat-icon { font-size: 32px; width: 32px; height: 32px; color: #7c8bff; }
    .stat-card.critical mat-icon { color: #ff4444; }
    .stat-card.score mat-icon { color: #44cc44; }
    .stat-value { font-size: 28px; font-weight: 700; margin: 8px 0 4px; }
    .stat-label { font-size: 13px; opacity: 0.7; }
    .chart-row { display: grid; grid-template-columns: 1fr; gap: 16px; }
    .chart-card { padding: 16px; max-width: 420px; }
  `]
})
export class DashboardComponent implements OnInit {
  loading = signal(true);
  data = signal<DashboardResponse | null>(null);

  doughnutData: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [] };
  chartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    plugins: { legend: { position: 'bottom', labels: { color: '#ccc' } } }
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getDashboard().subscribe(d => {
      this.data.set(d);
      this.doughnutData = {
        labels: ['Aprovados', 'Rejeitados', 'Críticos', 'Abertos'],
        datasets: [{
          data: [d.approvedPrs, d.rejectedPrs, d.criticalPrs, d.openPrs],
          backgroundColor: ['#44cc44', '#ff8844', '#ff4444', '#7c8bff']
        }]
      };
      this.loading.set(false);
    });
  }
}
