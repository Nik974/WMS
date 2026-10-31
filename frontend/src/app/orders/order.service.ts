import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type OrderStatus = 'NEW' | 'IN_TRANSIT' | 'SHIPPED' | 'CANCELLED';

export const ORDER_TRANSITIONS: Record<OrderStatus, OrderStatus[]> = {
  NEW: ['IN_TRANSIT', 'CANCELLED'],
  IN_TRANSIT: ['SHIPPED', 'CANCELLED'],
  SHIPPED: [],
  CANCELLED: [],
};

export const STATUS_LABELS: Record<OrderStatus, string> = {
  NEW: 'New',
  IN_TRANSIT: 'In transit',
  SHIPPED: 'Shipped',
  CANCELLED: 'Cancelled',
};

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
}

export interface Order {
  id: number;
  warehouseId: number;
  customerId: number;
  customerName: string;
  status: OrderStatus;
  createdAt: string;
  items: OrderItem[];
}

export interface CreateOrderRequest {
  warehouseId: number;
  customerId: number;
  items: { productId: number; quantity: number }[];
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private url = '/api/v1/outbound-orders';

  getAll(): Observable<Order[]> {
    return this.http.get<Order[]>(this.url);
  }

  create(req: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.url, req);
  }

  changeStatus(id: number, status: OrderStatus): Observable<Order> {
    return this.http.patch<Order>(`${this.url}/${id}/status`, null, { params: { status } });
  }
}
