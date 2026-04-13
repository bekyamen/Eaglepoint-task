import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, map, switchMap } from 'rxjs';
import { StopService } from '../../core/services/stop.service';
import { Stop } from '../../models/dto.models';

@Component({
  template: `
    <h2>Stop Details</h2>
    <ng-container *ngIf="stop$ | async as stop">
      <p><strong>Name:</strong> {{ stop.name }}</p>
      <p><strong>Popularity Score:</strong> {{ stop.popularityScore }}</p>
    </ng-container>
  `
})
export class PassengerStopDetailsPageComponent {
  readonly stop$: Observable<Stop>;

  constructor(activatedRoute: ActivatedRoute, stopService: StopService) {
    this.stop$ = activatedRoute.paramMap.pipe(
      map((params) => params.get('id') ?? ''),
      switchMap((id) => stopService.get(id)),
      map((response) => response.data)
    );
  }
}
