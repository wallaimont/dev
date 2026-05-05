import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./core/layout/layout.component').then(m => m.LayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'pull-requests', loadComponent: () => import('./features/pull-requests/pr-list.component').then(m => m.PrListComponent) },
      { path: 'pull-requests/:id', loadComponent: () => import('./features/pull-requests/pr-detail.component').then(m => m.PrDetailComponent) },
    ]
  },
  { path: '**', redirectTo: '' }
];
