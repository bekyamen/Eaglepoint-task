import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse, Stop } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class StopService {
  private readonly baseUrl = '/api/v1/stops';

  constructor(private readonly http: HttpClient) {}

  get(stopId: string): Observable<ApiResponse<Stop>> {
    return this.http.get<ApiResponse<Stop>>(`${this.baseUrl}/${stopId}`);
  }
}
