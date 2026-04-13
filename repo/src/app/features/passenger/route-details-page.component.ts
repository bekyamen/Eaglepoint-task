import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, map, switchMap } from 'rxjs';
import { RouteService } from '../../core/services/route.service';
import { Route } from '../../models/dto.models';

@Component({
  template: `
    <h2>Route Details</h2>
    <ng-container *ngIf="route$ | async as route">
      <p><strong>Name:</strong> {{ route.name }}</p>
      <p><strong>Frequency Score:</strong> {{ route.frequencyScore }}</p>
    </ng-container>
  `
})
export class PassengerRouteDetailsPageComponent {
  readonly route$: Observable<Route>;

  constructor(activatedRoute: ActivatedRoute, routeService: RouteService) {
    this.route$ = activatedRoute.paramMap.pipe(
      map((params) => params.get('id') ?? ''),
      switchMap((id) => routeService.get(id)),
      map((response) => response.data)
    );
  }
}
