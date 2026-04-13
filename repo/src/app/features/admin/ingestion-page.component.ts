import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AdminOpsService, DataVersion } from '../../core/services/admin-ops.service';

@Component({
  template: `
    <h2>Data Ingestion Dashboard</h2>
    <ul>
      <li *ngFor="let version of (versions$ | async)">
        {{ version.sourceName }} / {{ version.versionLabel }} / {{ version.active ? 'ACTIVE' : 'INACTIVE' }}
      </li>
    </ul>
  `
})
export class AdminIngestionPageComponent {
  readonly versions$: Observable<DataVersion[]>;

  constructor(adminOpsService: AdminOpsService) {
    this.versions$ = adminOpsService.listIngestions().pipe(map((response) => response.data));
  }
}
