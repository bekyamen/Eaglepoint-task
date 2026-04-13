import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/dto.models';

export interface SearchResultDto { entityId: string; entityType: string; name: string; score: number; }

@Injectable({ providedIn: 'root' })
export class SearchService {
  private readonly baseUrl = '/api/v1/search';
  constructor(private readonly http: HttpClient) {}

  search(q: string, type: 'route' | 'stop' | 'all'): Observable<ApiResponse<SearchResultDto[]>> {
    const params = new HttpParams().set('q', q).set('type', type);
    return this.http.get<ApiResponse<SearchResultDto[]>>(this.baseUrl, { params });
  }

  autocomplete(q: string): Observable<ApiResponse<SearchResultDto[]>> {
    return this.http.get<ApiResponse<SearchResultDto[]>>(`${this.baseUrl}/autocomplete`, { params: { q } });
  }
}
