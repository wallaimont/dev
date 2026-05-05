import { Routes } from '@angular/router';
import { ShellComponent } from './shell/shell.component';

export const DASHBOARD_ROUTES: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./ticket-list/ticket-list.component').then(m => m.TicketListComponent)
      },
      {
        path: 'tickets/new',
        loadComponent: () => import('./ticket-create/ticket-create.component').then(m => m.TicketCreateComponent)
      },
      {
        path: 'tickets/:id',
        loadComponent: () => import('./ticket-detail/ticket-detail.component').then(m => m.TicketDetailComponent)
      }
    ]
  }
];
