import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AdminOpsService, ConfigEntry } from '../../core/services/admin-ops.service';

@Component({
  template: `
    <h2>Notification Template Manager</h2>
    <ul>
      <li *ngFor="let template of (templates$ | async)">
        {{ template.key }}: {{ template.value }}
      </li>
    </ul>
  `
})
export class AdminTemplatesPageComponent {
  readonly templates$: Observable<ConfigEntry[]>;

  constructor(adminOpsService: AdminOpsService) {
    this.templates$ = adminOpsService.listTemplates().pipe(map((response) => response.data));
  }
}
