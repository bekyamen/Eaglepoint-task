import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutShellComponent } from './core/layout/layout-shell.component';
import { AuthGuard } from './core/security/auth.guard';
import { RoleGuard } from './core/security/role.guard';

const routes: Routes = [
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then((m) => m.AuthModule) },
  {
    path: '',
    component: LayoutShellComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'passenger', loadChildren: () => import('./features/passenger/passenger.module').then((m) => m.PassengerModule), canActivate: [RoleGuard], data: { roles: ['PASSENGER', 'DISPATCHER', 'ADMIN'] } },
      { path: 'dispatcher', loadChildren: () => import('./features/dispatcher/dispatcher.module').then((m) => m.DispatcherModule), canActivate: [RoleGuard], data: { roles: ['DISPATCHER', 'ADMIN'] } },
      { path: 'admin', loadChildren: () => import('./features/admin/admin.module').then((m) => m.AdminModule), canActivate: [RoleGuard], data: { roles: ['ADMIN'] } }
    ]
  },
  { path: '', pathMatch: 'full', redirectTo: 'passenger/search' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
