import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AdminOpsService, AuditLog } from '../../core/services/admin-ops.service';

@Component({
  template: `
    <h2>Audit Logs Viewer</h2>
    <ul>
      <li *ngFor="let log of (logs$ | async)">
        {{ log.moduleName }} - {{ log.action }} - {{ log.createdAt }}
      </li>
    </ul>
  `
})
export class AdminAuditPageComponent {
  readonly logs$: Observable<AuditLog[]>;

  constructor(adminOpsService: AdminOpsService) {
    this.logs$ = adminOpsService.listAuditLogs().pipe(map((response) => response.data));
  }
}
