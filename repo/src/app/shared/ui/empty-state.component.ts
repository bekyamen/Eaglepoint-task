import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  template: '<mat-card><p>{{message}}</p></mat-card>'
})
export class EmptyStateComponent {
  @Input() message = 'No data available.';
}
