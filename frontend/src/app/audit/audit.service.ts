import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface AuditEntry {
  id: number;
  createdAt: string;
  username: string;
  email: string;
  httpMethod: string;
  resource: string;
  resourceId: number | null;
  action: string | null;
  query: string | null;
  description: string | null;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  number: number;
  size: number;
}

export interface AuditFilter {
  user: string;
  resource: string;
  from: string;
  to: string;
}

@Injectable({providedIn: 'root'})
export class AuditService {
  private http = inject(HttpClient);
  private url = '/api/v1/audit';

  search(f: AuditFilter, page: number, size: number): Observable<Page<AuditEntry>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (f.user.trim()) params = params.set('user', f.user.trim());
    if (f.resource) params = params.set('resource', f.resource);
    if (f.from) params = params.set('from', f.from);
    if (f.to) params = params.set('to', f.to);
    return this.http.get<Page<AuditEntry>>(this.url, {params});
  }
}
