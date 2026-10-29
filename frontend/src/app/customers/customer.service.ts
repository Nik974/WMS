import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Customer {
  customerId: number;
  customerName: string;
}

export interface CustomerRequest {
  customerName: string;
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private http = inject(HttpClient);
  private url = '/api/v1/customers';

  getAll(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.url);
  }

  create(req: CustomerRequest): Observable<Customer> {
    return this.http.post<Customer>(this.url, req);
  }

  update(id: number, req: CustomerRequest): Observable<Customer> {
    return this.http.put<Customer>(`${this.url}/${id}`, req);
  }
}