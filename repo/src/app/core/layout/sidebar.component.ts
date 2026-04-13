import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  template: `
    <mat-nav-list>
      <a mat-list-item routerLink="/passenger/search">Search</a>
      <a mat-list-item routerLink="/passenger/notifications">Notifications</a>
      <a mat-list-item routerLink="/dispatcher/tasks" *ngIf="(isDispatcher$ | async)">Tasks</a>
      <a mat-list-item routerLink="/admin/config" *ngIf="(isAdmin$ | async)">Admin Config</a>
    </mat-nav-list>
  `
})
export class SidebarComponent {
  readonly isDispatcher$;
  readonly isAdmin$;
  constructor(private readonly authService: AuthService) {
    this.isDispatcher$ = this.authService.currentUser$.pipe(map((u) => u?.role === 'DISPATCHER' || u?.role === 'ADMIN'));
    this.isAdmin$ = this.authService.currentUser$.pipe(map((u) => u?.role === 'ADMIN'));
  }
}
