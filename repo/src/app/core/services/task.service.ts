import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse, Task } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly baseUrl = '/api/v1/tasks';
  constructor(private readonly http: HttpClient) {}
  list(status?: string): Observable<ApiResponse<Task[]>> {
    const params = status ? new HttpParams().set('status', status) : undefined;
    return this.http.get<ApiResponse<Task[]>>(this.baseUrl, { params });
  }

  get(taskId: string): Observable<ApiResponse<Task>> {
    return this.http.get<ApiResponse<Task>>(`${this.baseUrl}/${taskId}`);
  }
}
