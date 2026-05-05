import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { PullRequestResponse, DashboardResponse, FinalAnalysisResponse, Page } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.base}/dashboard`);
  }

  getPullRequests(page = 0, size = 20): Observable<Page<PullRequestResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<PullRequestResponse>>(`${this.base}/pull-requests`, { params });
  }

  getPullRequest(id: string): Observable<PullRequestResponse> {
    return this.http.get<PullRequestResponse>(`${this.base}/pull-requests/${id}`);
  }

  getAnalyses(prId: string): Observable<FinalAnalysisResponse[]> {
    return this.http.get<FinalAnalysisResponse[]>(`${this.base}/pull-requests/${prId}/analyses`);
  }
}
