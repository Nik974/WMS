import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Supplier {
  supplierId: number;
  supplierName: string;
  taxId: string;
}

export interface SupplierRequest {
  supplierName: string;
  taxId: string;
}

@Injectable({ providedIn: 'root' })
export class SupplierService {
  private http = inject(HttpClient);
  private url = '/api/v1/suppliers';

  getAll(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(this.url);
  }

  create(req: SupplierRequest): Observable<Supplier> {
    return this.http.post<Supplier>(this.url, req);
  }

  update(id: number, req: SupplierRequest): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.url}/${id}`, req);
  }
}
