import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, map, switchMap } from 'rxjs';
import { TaskService } from '../../core/services/task.service';
import { Task } from '../../models/dto.models';

@Component({
  template: `
    <h2>Task Detail</h2>
    <ng-container *ngIf="task$ | async as task">
      <p><strong>Type:</strong> {{ task.type }}</p>
      <p><strong>Status:</strong> {{ task.status }}</p>
      <p><strong>Timeout:</strong> {{ task.timeoutAt || 'N/A' }}</p>
    </ng-container>
  `
})
export class DispatcherTaskDetailPageComponent {
  readonly task$: Observable<Task>;

  constructor(activatedRoute: ActivatedRoute, taskService: TaskService) {
    this.task$ = activatedRoute.paramMap.pipe(
      map((params) => params.get('id') ?? ''),
      switchMap((id) => taskService.get(id)),
      map((response) => response.data)
    );
  }
}
