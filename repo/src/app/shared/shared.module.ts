import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { EmptyStateComponent } from './ui/empty-state.component';
import { LoadingSpinnerComponent } from './ui/loading-spinner.component';

@NgModule({
  declarations: [EmptyStateComponent, LoadingSpinnerComponent],
  imports: [CommonModule, MatProgressSpinnerModule, MatCardModule],
  exports: [CommonModule, EmptyStateComponent, LoadingSpinnerComponent]
})
export class SharedModule {}
