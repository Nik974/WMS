import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  productId: number;
  productName: string;
  productSku: string;
  unit: string | null;
  categoryId: number;
  categoryName: string;
}

export interface ProductRequest {
  productName: string;
  productSku: string;
  unit: string | null;
  categoryId: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private url = '/api/v1/products';

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.url);
  }

  create(req: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.url, req);
  }

  update(id: number, req: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.url}/${id}`, req);
  }
}
