import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { WorkflowService } from '../../core/services/workflow.service';
import { WorkflowState } from '../../models/dto.models';

@Component({
  template: `
    <h2>Workflow Viewer</h2>
    <ul><li *ngFor="let state of (states$ | async)">{{ state.workflowName }} - {{ state.currentState }}</li></ul>
  `
})
export class DispatcherWorkflowPageComponent {
  readonly states$: Observable<WorkflowState[]>;
  constructor(private readonly workflowService: WorkflowService) {
    this.states$ = this.workflowService.listStates().pipe(map((r) => r.data));
  }
}
