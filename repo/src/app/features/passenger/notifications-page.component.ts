import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Notification } from '../../models/dto.models';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  template: `
    <h2>Notifications</h2>
    <button mat-button (click)="markAll()">Mark all as read</button>
    <mat-list>
      <mat-list-item *ngFor="let item of (notifications$ | async)">
        {{ item.type }} - {{ item.content }} ({{ item.status }})
      </mat-list-item>
    </mat-list>
  `
})
export class PassengerNotificationsPageComponent {
  readonly notifications$: Observable<Notification[]> = new Observable((subscriber) => {
    this.notificationService.list().subscribe((res) => {
      subscriber.next(res.data);
      subscriber.complete();
    });
  });

  constructor(private readonly notificationService: NotificationService) {}
  markAll(): void { this.notificationService.markAllAsRead().subscribe(); }
}
