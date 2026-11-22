import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface Stock {
  stockId: number;
  productId: number;
  productName: string | null;
  productSku: string | null;
  locationId: number;
  locationCode: string | null;
  quantity: number;
  reservedQuantity: number;
  available: number;
  batchId: number;
  lotNumber: string | null;
  expiryDate: string | null;
}

export interface AdjustRequest {
  locationId: number;
  productId: number;
  batchId: number;
  newQuantity: number;
  reason: string;
}

@Injectable({providedIn: 'root'})
export class StockService {
  private http = inject(HttpClient);
  private url = '/api/v1/stocks';

  getAll(warehouseId: number | null): Observable<Stock[]> {
    let params = new HttpParams();
    if (warehouseId !== null) params = params.set('warehouseId', warehouseId);
    return this.http.get<Stock[]>(this.url, {params});
  }

  adjust(req: AdjustRequest): Observable<Stock> {
    return this.http.post<Stock>(`${this.url}/adjustments`, req);
  }
}
