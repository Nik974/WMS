import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Warehouse {
  id: number;
  warehouseName: string;
  warehouseAddress: string | null;
}

export interface WarehouseRequest {
  warehouseName: string;
  warehouseAddress: string | null;
}

@Injectable({ providedIn: 'root' })
export class WarehouseService {
  private http = inject(HttpClient);
  private url = '/api/v1/warehouses';

  getAll(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(this.url);
  }

  create(req: WarehouseRequest): Observable<Warehouse> {
    return this.http.post<Warehouse>(this.url, req);
  }

  update(id: number, req: WarehouseRequest): Observable<Warehouse> {
    return this.http.put<Warehouse>(`${this.url}/${id}`, req);
  }
}
