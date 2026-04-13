import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DispatcherTasksPageComponent } from './tasks-page.component';
import { DispatcherWorkflowPageComponent } from './workflow-page.component';
import { DispatcherTaskDetailPageComponent } from './task-detail-page.component';

const routes: Routes = [
  { path: 'tasks', component: DispatcherTasksPageComponent },
  { path: 'tasks/:id', component: DispatcherTaskDetailPageComponent },
  { path: 'workflow', component: DispatcherWorkflowPageComponent }
];

@NgModule({
  declarations: [DispatcherTasksPageComponent, DispatcherWorkflowPageComponent, DispatcherTaskDetailPageComponent],
  imports: [CommonModule, RouterModule.forChild(routes)]
})
export class DispatcherModule {}
