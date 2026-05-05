import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatSidenavModule, MatListModule, MatIconModule, MatButtonModule],
  template: `
    <mat-toolbar color="primary" class="toolbar">
      <mat-icon class="logo-icon">smart_toy</mat-icon>
      <span class="brand">PR Assistant AI</span>
      <span class="spacer"></span>
      <span class="username">{{ auth.username() }}</span>
      <button mat-icon-button (click)="auth.logout()" aria-label="Sair">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>

    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav mode="side" opened class="sidenav">
        <mat-nav-list>
          <a mat-list-item routerLink="/dashboard" routerLinkActive="active">
            <mat-icon matListItemIcon>dashboard</mat-icon>
            <span matListItemTitle>Dashboard</span>
          </a>
          <a mat-list-item routerLink="/pull-requests" routerLinkActive="active">
            <mat-icon matListItemIcon>merge_type</mat-icon>
            <span matListItemTitle>Pull Requests</span>
          </a>
        </mat-nav-list>
      </mat-sidenav>

      <mat-sidenav-content class="content">
        <div class="container">
          <router-outlet />
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    .toolbar { position: fixed; z-index: 10; }
    .logo-icon { margin-right: 8px; }
    .brand { font-weight: 600; font-size: 18px; }
    .spacer { flex: 1; }
    .username { margin-right: 8px; font-size: 14px; opacity: 0.8; }
    .sidenav-container { position: absolute; top: 64px; bottom: 0; left: 0; right: 0; }
    .sidenav { width: 220px; background: #15152e; }
    .content { padding: 24px; }
    .active { background: rgba(124,139,255,0.15) !important; }
  `]
})
export class LayoutComponent {
  constructor(public auth: AuthService) {}
}
