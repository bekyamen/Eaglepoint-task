import { Component } from '@angular/core';

@Component({
  selector: 'app-layout-shell',
  template: `
    <mat-sidenav-container>
      <mat-sidenav mode="side" opened>
        <app-sidebar></app-sidebar>
      </mat-sidenav>
      <mat-sidenav-content>
        <app-topbar></app-topbar>
        <main class="content"><router-outlet></router-outlet></main>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: ['.content{padding:16px;} mat-sidenav-container{height:100vh;}']
})
export class LayoutShellComponent {}
