import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-topbar',
  template: `
    <mat-toolbar color="primary">
      <span>City Bus Operation Platform</span>
      <span class="spacer"></span>
      <button mat-icon-button [matBadge]="(unreadCount$ | async) ?? 0" matBadgeColor="warn">
        <mat-icon>notifications</mat-icon>
      </button>
      <button mat-button (click)="logout()">Logout</button>
    </mat-toolbar>
  `,
  styles: ['.spacer{flex:1 1 auto;}']
})
export class TopbarComponent {
  readonly unreadCount$;
  constructor(private readonly authService: AuthService, private readonly notificationService: NotificationService) {
    this.unreadCount$ = this.notificationService.unreadCount$;
  }
  logout(): void { this.authService.logout(); }
}
