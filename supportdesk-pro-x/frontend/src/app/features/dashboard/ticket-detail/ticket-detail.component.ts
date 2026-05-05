import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TicketService } from '../../../core/services/ticket.service';
import { CommentService } from '../../../core/services/comment.service';
import { TicketResponse } from '../../../core/models/ticket.model';
import { CommentResponse } from '../../../core/models/comment.model';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatDividerModule, MatFormFieldModule, MatInputModule, MatProgressBarModule
  ],
  template: `
    @if (loading) { <mat-progress-bar mode="indeterminate"/> }
    @if (ticket) {
      <mat-card class="detail-card">
        <mat-card-header>
          <mat-card-title>[{{ ticket.ticketNumber }}] {{ ticket.title }}</mat-card-title>
          <mat-card-subtitle>
            <span [class]="'status-chip ' + ticket.status">{{ ticket.status }}</span>
            <span [class]="'priority-chip ' + ticket.priority" style="margin-left:8px">{{ ticket.priority }}</span>
          </mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <p>{{ ticket.description }}</p>
          <p><strong>Categoria:</strong> {{ ticket.category.name }}</p>
          <p><strong>Solicitante:</strong> {{ ticket.requester.fullName }}</p>
          @if (ticket.assignee) {
            <p><strong>Responsável:</strong> {{ ticket.assignee.fullName }}</p>
          }
          @if (ticket.slaDeadline) {
            <p><strong>SLA:</strong> {{ ticket.slaDeadline | date:'dd/MM/yy HH:mm' }}</p>
          }
          <p><strong>Criado em:</strong> {{ ticket.createdAt | date:'dd/MM/yy HH:mm' }}</p>
        </mat-card-content>
      </mat-card>

      <h3 style="margin-top:24px">Comentários</h3>
      <mat-divider/>
      <div class="comments">
        @for (c of comments; track c.id) {
          <div class="comment-item">
            <strong>{{ c.author.fullName }}</strong>
            <span class="comment-date">{{ c.createdAt | date:'dd/MM/yy HH:mm' }}</span>
            <p>{{ c.content }}</p>
          </div>
        }
      </div>

      <form [formGroup]="commentForm" (ngSubmit)="addComment()" class="comment-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Adicionar comentário</mat-label>
          <textarea matInput formControlName="content" rows="3"></textarea>
        </mat-form-field>
        <button mat-raised-button color="accent" type="submit" [disabled]="commentForm.invalid">
          <mat-icon>send</mat-icon> Enviar
        </button>
      </form>
    }
  `,
  styles: [`
    .detail-card { margin-bottom: 24px; }
    .full-width { width: 100%; }
    .comments { margin: 16px 0; }
    .comment-item { padding: 12px 0; border-bottom: 1px solid #e0e0e0; }
    .comment-date { color: #777; font-size: 0.8rem; margin-left: 8px; }
    .comment-form { margin-top: 16px; }
  `]
})
export class TicketDetailComponent implements OnInit {
  ticket?: TicketResponse;
  comments: CommentResponse[] = [];
  commentForm!: FormGroup;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private ticketSvc: TicketService,
    private commentSvc: CommentService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.commentForm = this.fb.group({ content: ['', Validators.required] });
    const id = this.route.snapshot.paramMap.get('id')!;
    this.loading = true;
    this.ticketSvc.get(id).subscribe({
      next: t => {
        this.ticket = t;
        this.loading = false;
        this.loadComments(id);
      },
      error: () => { this.loading = false; this.router.navigate(['/']); }
    });
  }

  loadComments(id: string): void {
    this.commentSvc.list(id).subscribe({ next: c => this.comments = c });
  }

  addComment(): void {
    if (this.commentForm.invalid || !this.ticket) return;
    const req = { content: this.commentForm.value.content, internal: false };
    this.commentSvc.create(this.ticket.id, req).subscribe({
      next: c => {
        this.comments.push(c);
        this.commentForm.reset();
      }
    });
  }
}
