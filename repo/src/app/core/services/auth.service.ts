import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponse, AuthResponse, Role, User } from '../../models/dto.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = '/api/v1';
  private readonly tokenSubject = new BehaviorSubject<string | null>(null);
  private readonly userSubject = new BehaviorSubject<User | null>(null);
  readonly currentUser$ = this.userSubject.asObservable();

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  login(username: string, password: string): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/login`, { username, password }).pipe(
      tap((res) => {
        this.tokenSubject.next(res.data.token);
        this.userSubject.next(res.data.user);
      })
    );
  }

  register(username: string, password: string): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/register`, { username, password });
  }

  me(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.baseUrl}/users/me`).pipe(tap((res) => this.userSubject.next(res.data)));
  }

  logout(): void {
    this.tokenSubject.next(null);
    this.userSubject.next(null);
    this.router.navigateByUrl('/auth/login');
  }

  get token(): string | null { return this.tokenSubject.value; }
  get currentRole(): Role | null { return this.userSubject.value?.role ?? null; }
  isAuthenticated(): boolean { return !!this.tokenSubject.value; }
}
