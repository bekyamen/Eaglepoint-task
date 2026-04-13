import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  template: `
    <h2>Notification Preferences</h2>
    <form [formGroup]="form">
      <label><input type="checkbox" formControlName="arrivalReminders"> Arrival reminders</label>
      <label><input type="checkbox" formControlName="dndEnabled"> DND enabled</label>
      <input type="time" formControlName="dndStart">
      <input type="time" formControlName="dndEnd">
    </form>
  `
})
export class PassengerPreferencesPageComponent {
  readonly form;
  constructor(private readonly fb: FormBuilder) {
    this.form = this.fb.group({ arrivalReminders: true, dndEnabled: false, dndStart: '22:00', dndEnd: '06:00' });
  }
}
