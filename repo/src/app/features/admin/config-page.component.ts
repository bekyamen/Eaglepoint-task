import { Component } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';

@Component({
  template: `
    <h2>System Config Dashboard</h2>
    <ul><li *ngFor="let cfg of (configs$ | async)">{{ cfg | json }}</li></ul>
  `
})
export class AdminConfigPageComponent {
  readonly configs$: Observable<Record<string, string>[]>;
  constructor(private readonly configService: ConfigService) {
    this.configs$ = this.configService.list().pipe(map((r) => r.data));
  }
}
