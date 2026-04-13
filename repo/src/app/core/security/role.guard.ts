import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}
  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expected = route.data['roles'] as string[];
    const current = this.authService.currentRole;
    if (current && expected.includes(current)) { return true; }
    this.router.navigateByUrl('/auth/login');
    return false;
  }
}
