import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private readonly baseUrl = '/api/v1/config';
  constructor(private readonly http: HttpClient) {}
  list(): Observable<ApiResponse<Record<string, string>[]>> {
    return this.http.get<ApiResponse<Record<string, string>[]>>(this.baseUrl);
  }
}
