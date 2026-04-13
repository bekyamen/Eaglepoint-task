import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { TaskService } from '../../core/services/task.service';
import { Task } from '../../models/dto.models';

@Component({
  template: `
    <h2>Task Dashboard</h2>
    <ul><li *ngFor="let task of (tasks$ | async)">{{ task.type }} - {{ task.status }}</li></ul>
  `
})
export class DispatcherTasksPageComponent {
  readonly tasks$: Observable<Task[]>;
  constructor(private readonly taskService: TaskService) {
    this.tasks$ = this.taskService.list().pipe(map((r) => r.data));
  }
}
