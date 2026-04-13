import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AdminOpsService, IngestionEntry } from '../../core/services/admin-ops.service';

@Component({
  template: `
    <h2>Data Ingestion Dashboard</h2>
    <ul>
      <li *ngFor="let ingestion of (ingestions$ | async)">
        {{ ingestion.sourceName }} / {{ ingestion.versionLabel }} / {{ ingestion.ingestStatus }} / {{ ingestion.receivedAt | date:'short' }}
      </li>
    </ul>
  `
})
export class AdminIngestionPageComponent {
  readonly ingestions$: Observable<IngestionEntry[]>;

  constructor(adminOpsService: AdminOpsService) {
    this.ingestions$ = adminOpsService.listIngestions().pipe(map((response) => response.data));
  }
}
