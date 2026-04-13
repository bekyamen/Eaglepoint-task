import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/dto.models';

export interface ConfigEntry {
  key: string;
  value: string;
  group: string;
}

export interface DataVersion {
  id: string;
  sourceName: string;
  versionLabel: string;
  active: boolean;
}

export interface IngestionEntry {
  id: string;
  sourceName: string;
  ingestStatus: string;
  versionLabel: string;
  receivedAt: string;
}

export interface AuditLog {
  id: string;
  moduleName: string;
  action: string;
  entityType: string;
  entityId: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminOpsService {
  constructor(private readonly http: HttpClient) {}

  listTemplates(): Observable<ApiResponse<ConfigEntry[]>> {
    return this.http.get<ApiResponse<ConfigEntry[]>>('/api/v1/config/templates');
  }

  listIngestions(): Observable<ApiResponse<IngestionEntry[]>> {
    return this.http.get<ApiResponse<IngestionEntry[]>>('/api/v1/admin/data/ingestions');
  }

  listAuditLogs(): Observable<ApiResponse<AuditLog[]>> {
    return this.http.get<ApiResponse<AuditLog[]>>('/api/v1/admin/observability/audit-logs');
  }
}
