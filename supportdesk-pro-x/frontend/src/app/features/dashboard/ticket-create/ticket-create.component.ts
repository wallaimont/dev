import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { TicketService } from '../../../core/services/ticket.service';
import { ApiResponse } from '../../../core/models/api.model';

interface Category { id: string; name: string; }

@Component({
  selector: 'app-ticket-create',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatChipsModule, MatIconModule
  ],
  template: `
    <mat-card style="max-width:680px; margin: auto">
      <mat-card-header>
        <mat-card-title>Abrir Novo Chamado</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <form [formGroup]="form" (ngSubmit)="submit()">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Título</mat-label>
            <input matInput formControlName="title">
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Descrição</mat-label>
            <textarea matInput formControlName="description" rows="5"></textarea>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Categoria</mat-label>
            <mat-select formControlName="categoryId">
              @for (cat of categories; track cat.id) {
                <mat-option [value]="cat.id">{{ cat.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Prioridade</mat-label>
            <mat-select formControlName="priority">
              <mat-option value="LOW">Baixa</mat-option>
              <mat-option value="MEDIUM">Média</mat-option>
              <mat-option value="HIGH">Alta</mat-option>
              <mat-option value="CRITICAL">Crítica</mat-option>
            </mat-select>
          </mat-form-field>

          @if (error) { <p class="error-msg">{{ error }}</p> }

          <div class="actions">
            <button mat-button type="button" (click)="router.navigate(['/'])">Cancelar</button>
            <button mat-raised-button color="primary" type="submit" [disabled]="form.invalid || loading">
              Criar Chamado
            </button>
          </div>
        </form>
      </mat-card-content>
    </mat-card>
  `,
  styles: [`
    .full-width { width: 100%; }
    .actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
    .error-msg { color: #c62828; font-size: 0.85rem; }
  `]
})
export class TicketCreateComponent implements OnInit {
  form!: FormGroup;
  categories: Category[] = [];
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    public router: Router,
    private ticketSvc: TicketService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      title:       ['', [Validators.required, Validators.minLength(5)]],
      description: ['', Validators.required],
      categoryId:  ['', Validators.required],
      priority:    ['MEDIUM', Validators.required]
    });
    this.http
      .get<ApiResponse<Category[]>>(`${environment.apiUrl}/categories`)
      .subscribe({ next: r => this.categories = r.data });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.ticketSvc.create(this.form.value).subscribe({
      next: t => this.router.navigate(['/tickets', t.id]),
      error: err => {
        this.error = err?.error?.message ?? 'Erro ao criar chamado';
        this.loading = false;
      }
    });
  }
}
