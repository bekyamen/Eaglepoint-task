import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  template: `
    <mat-card>
      <h2>Register</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <mat-form-field><input matInput formControlName="username" placeholder="Username"></mat-form-field>
        <mat-form-field><input matInput type="password" formControlName="password" placeholder="Password"></mat-form-field>
        <button mat-raised-button color="primary" type="submit">Create Account</button>
      </form>
    </mat-card>
  `
})
export class RegisterPageComponent {
  readonly form;
  constructor(private readonly fb: FormBuilder, private readonly authService: AuthService, private readonly router: Router) {
    this.form = this.fb.group({ username: ['', Validators.required], password: ['', Validators.required] });
  }
  submit(): void {
    const { username, password } = this.form.getRawValue();
    if (!username || !password) { return; }
    this.authService.register(username, password).subscribe(() => this.router.navigateByUrl('/auth/login'));
  }
}
