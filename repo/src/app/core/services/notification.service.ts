import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval, switchMap } from 'rxjs';
import { ApiResponse, Notification } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly baseUrl = '/api/v1/notifications';
  private readonly unreadCountSubject = new BehaviorSubject<number>(0);
  readonly unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private readonly http: HttpClient) {
    interval(30000).pipe(switchMap(() => this.list())).subscribe({
      next: (res) => this.unreadCountSubject.next(res.data.filter((n) => n.status !== 'READ').length),
      error: () => this.unreadCountSubject.next(0)
    });
  }

  list(): Observable<ApiResponse<Notification[]>> {
    return this.http.get<ApiResponse<Notification[]>>(this.baseUrl);
  }

  markAsRead(notificationId: string): Observable<ApiResponse<Notification>> {
    return this.http.post<ApiResponse<Notification>>(`${this.baseUrl}/${notificationId}/read`, {});
  }

  markAllAsRead(): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.baseUrl}/read-all`, {});
  }
}
