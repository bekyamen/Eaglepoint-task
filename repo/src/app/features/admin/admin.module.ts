import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminConfigPageComponent } from './config-page.component';
import { AdminAuditPageComponent } from './audit-page.component';
import { AdminTemplatesPageComponent } from './templates-page.component';
import { AdminIngestionPageComponent } from './ingestion-page.component';

const routes: Routes = [
  { path: 'config', component: AdminConfigPageComponent },
  { path: 'templates', component: AdminTemplatesPageComponent },
  { path: 'ingestion', component: AdminIngestionPageComponent },
  { path: 'audit-logs', component: AdminAuditPageComponent }
];

@NgModule({
  declarations: [AdminConfigPageComponent, AdminAuditPageComponent, AdminTemplatesPageComponent, AdminIngestionPageComponent],
  imports: [CommonModule, RouterModule.forChild(routes)]
})
export class AdminModule {}
