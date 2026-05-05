import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CommentResponse, CreateCommentRequest } from '../models/comment.model';
import { ApiResponse } from '../models/api.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly base = `${environment.apiUrl}/tickets`;

  constructor(private http: HttpClient) {}

  list(ticketId: string, includeInternal = false): Observable<CommentResponse[]> {
    return this.http
      .get<ApiResponse<CommentResponse[]>>(`${this.base}/${ticketId}/comments`, {
        params: { includeInternal: String(includeInternal) }
      })
      .pipe(map(r => r.data));
  }

  create(ticketId: string, req: CreateCommentRequest): Observable<CommentResponse> {
    return this.http
      .post<ApiResponse<CommentResponse>>(`${this.base}/${ticketId}/comments`, req)
      .pipe(map(r => r.data));
  }
}
