import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse, WorkflowState } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class WorkflowService {
  private readonly baseUrl = '/api/v1/workflow';
  constructor(private readonly http: HttpClient) {}
  listStates(): Observable<ApiResponse<WorkflowState[]>> {
    return this.http.get<ApiResponse<WorkflowState[]>>(this.baseUrl);
  }
}
