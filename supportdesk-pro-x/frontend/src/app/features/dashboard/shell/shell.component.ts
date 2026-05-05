import { Component } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    CommonModule, RouterOutlet, RouterLink, RouterLinkActive,
    MatToolbarModule, MatSidenavModule, MatListModule,
    MatIconModule, MatButtonModule
  ],
  template: `
    <mat-toolbar color="primary" class="toolbar">
      <span>SupportDesk Pro X</span>
      <span class="spacer"></span>
      <span class="user-name">{{ (authSvc.currentUser$ | async)?.fullName }}</span>
      <button mat-icon-button (click)="logout()" title="Sair">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>

    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav mode="side" opened class="sidenav">
        <mat-nav-list>
          <a mat-list-item routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">
            <mat-icon matListItemIcon>confirmation_number</mat-icon>
            <span matListItemTitle>Meus Chamados</span>
          </a>
          <a mat-list-item routerLink="/tickets/new">
            <mat-icon matListItemIcon>add_circle</mat-icon>
            <span matListItemTitle>Novo Chamado</span>
          </a>
        </mat-nav-list>
      </mat-sidenav>

      <mat-sidenav-content class="main-content">
        <router-outlet/>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    .toolbar { position: sticky; top: 0; z-index: 900; }
    .sidenav-container { height: calc(100vh - 64px); }
    .sidenav { width: 220px; padding-top: 8px; }
    .main-content { padding: 24px; }
    .user-name { margin-right: 8px; font-size: 0.9rem; }
    a.active { background: rgba(255,255,255,0.15); }
  `]
})
export class ShellComponent {
  constructor(public authSvc: AuthService, private router: Router) {}

  logout(): void {
    this.authSvc.logout();
    this.router.navigate(['/auth/login']);
  }
}
