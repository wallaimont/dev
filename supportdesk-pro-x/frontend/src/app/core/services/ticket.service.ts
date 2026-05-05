import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateTicketRequest,
  PageResponse,
  TicketResponse,
  UpdateTicketRequest
} from '../models/ticket.model';
import { ApiResponse } from '../models/api.model';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly base = `${environment.apiUrl}/tickets`;

  constructor(private http: HttpClient) {}

  list(page = 0, size = 20): Observable<PageResponse<TicketResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<PageResponse<TicketResponse>>>(this.base, { params })
      .pipe(map(r => r.data));
  }

  get(id: string): Observable<TicketResponse> {
    return this.http
      .get<ApiResponse<TicketResponse>>(`${this.base}/${id}`)
      .pipe(map(r => r.data));
  }

  create(req: CreateTicketRequest): Observable<TicketResponse> {
    return this.http
      .post<ApiResponse<TicketResponse>>(`${this.base}`, req)
      .pipe(map(r => r.data));
  }

  update(id: string, req: UpdateTicketRequest): Observable<TicketResponse> {
    return this.http
      .patch<ApiResponse<TicketResponse>>(`${this.base}/${id}`, req)
      .pipe(map(r => r.data));
  }
}
