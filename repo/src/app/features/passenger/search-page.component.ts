import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged, filter, switchMap } from 'rxjs/operators';
import { SearchResultDto, SearchService } from '../../core/services/search.service';

@Component({
  template: `
    <h2>Search Routes and Stops</h2>
    <input matInput [formControl]="queryControl" placeholder="Search routes, stops, pinyin, initials">
    <mat-list>
      <mat-list-item *ngFor="let item of results">
        {{ item.name }} ({{ item.entityType }}) <mat-chip>{{ item.score | number: '1.0-2' }}</mat-chip>
      </mat-list-item>
    </mat-list>
  `
})
export class PassengerSearchPageComponent {
  readonly queryControl = new FormControl('', { nonNullable: true });
  results: SearchResultDto[] = [];

  constructor(private readonly searchService: SearchService) {
    this.queryControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        filter((q) => q.trim().length > 0),
        switchMap((q) => this.searchService.search(q, 'all'))
      )
      .subscribe((res) => (this.results = res.data));
  }
}
