import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse, Route } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class RouteService {
  private readonly baseUrl = '/api/v1/routes';

  constructor(private readonly http: HttpClient) {}

  get(routeId: string): Observable<ApiResponse<Route>> {
    return this.http.get<ApiResponse<Route>>(`${this.baseUrl}/${routeId}`);
  }
}
