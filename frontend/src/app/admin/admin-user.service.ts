import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type Role = 'WORKER' | 'MANAGER' | 'ADMIN';
export const ROLES: Role[] = ['WORKER', 'MANAGER', 'ADMIN'];

export interface AppUser {
  id: number;
  username: string;
  email: string;
  role: string;
  enabled: boolean;
  createdAt: string | null;
}

export interface UpdateUserRequest {
  username: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private http = inject(HttpClient);
  private url = '/api/v1/admin/users';

  getAll(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.url);
  }

  update(id: number, req: UpdateUserRequest): Observable<AppUser> {
    return this.http.put<AppUser>(`${this.url}/${id}`, req);
  }

  setBlocked(id: number, blocked: boolean): Observable<AppUser> {
    return this.http.patch<AppUser>(`${this.url}/${id}/${blocked ? 'block' : 'unblock'}`, null);
  }
}
