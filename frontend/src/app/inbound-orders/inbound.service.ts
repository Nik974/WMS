import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type InboundStatus = 'NEW' | 'RECEIVED' | 'CANCELLED';

export const INBOUND_TRANSITIONS: Record<InboundStatus, InboundStatus[]> = {
  NEW: ['RECEIVED', 'CANCELLED'],
  RECEIVED: [],
  CANCELLED: [],
};

export const INBOUND_LABELS: Record<InboundStatus, string> = {
  NEW: 'New',
  RECEIVED: 'Received',
  CANCELLED: 'Cancelled',
};

export interface InboundItem {
  id: number;
  productId: number;
  productName: string | null;
  quantity: number;
}

export interface InboundOrder {
  orderId: number;
  warehouseId: number;
  supplierId: number;
  supplierName: string;
  status: InboundStatus;
  createdAt: string;
  items: InboundItem[];
}

export interface CreateInboundRequest {
  warehouseId: number;
  supplierId: number;
  items: { productId: number; quantity: number }[];
}

@Injectable({ providedIn: 'root' })
export class InboundService {
  private http = inject(HttpClient);
  private url = '/api/v1/inbound-orders';

  getAll(): Observable<InboundOrder[]> {
    return this.http.get<InboundOrder[]>(this.url);
  }

  create(req: CreateInboundRequest): Observable<InboundOrder> {
    return this.http.post<InboundOrder>(this.url, req);
  }

  changeStatus(id: number, status: InboundStatus): Observable<InboundOrder> {
    return this.http.patch<InboundOrder>(`${this.url}/${id}/status`, null, { params: { status } });
  }
}
