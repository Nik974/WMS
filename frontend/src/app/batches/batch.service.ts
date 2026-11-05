import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Batch {
  batchId: number;
  productId: number;
  lotNumber: string;
  expiryDate: string | null;
  supplierId: number | null;
}

export interface BatchRequest {
  productId: number;
  lotNumber: string;
  expiryDate: string | null;
  supplierId: number | null;
}

@Injectable({ providedIn: 'root' })
export class BatchService {
  private http = inject(HttpClient);
  private url = '/api/v1/batches';

  getAll(): Observable<Batch[]> {
    return this.http.get<Batch[]>(this.url);
  }

  create(req: BatchRequest): Observable<Batch> {
    return this.http.post<Batch>(this.url, req);
  }

  update(id: number, req: BatchRequest): Observable<Batch> {
    return this.http.put<Batch>(`${this.url}/${id}`, req);
  }
}
