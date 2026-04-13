import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { PassengerSearchPageComponent } from './search-page.component';
import { PassengerNotificationsPageComponent } from './notifications-page.component';
import { PassengerPreferencesPageComponent } from './preferences-page.component';
import { PassengerRouteDetailsPageComponent } from './route-details-page.component';
import { PassengerStopDetailsPageComponent } from './stop-details-page.component';

const routes: Routes = [
  { path: 'search', component: PassengerSearchPageComponent },
  { path: 'routes/:id', component: PassengerRouteDetailsPageComponent },
  { path: 'stops/:id', component: PassengerStopDetailsPageComponent },
  { path: 'notifications', component: PassengerNotificationsPageComponent },
  { path: 'preferences', component: PassengerPreferencesPageComponent }
];

@NgModule({
  declarations: [
    PassengerSearchPageComponent,
    PassengerNotificationsPageComponent,
    PassengerPreferencesPageComponent,
    PassengerRouteDetailsPageComponent,
    PassengerStopDetailsPageComponent
  ],
  imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes), MatInputModule, MatListModule, MatChipsModule, MatButtonModule]
})
export class PassengerModule {}
