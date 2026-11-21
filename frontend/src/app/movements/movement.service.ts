import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface Movement {
  id: number;
  productId: number;
  productName: string | null;
  fromLocationId: number | null;
  fromLocationCode: string | null;
  toLocationId: number | null;
  toLocationCode: string | null;
  batchId: number | null;
  lotNumber: string | null;
  userId: number;
  username: string;
  movementType: string;
  quantity: number;
  referenceType: string | null;
  referenceId: number | null;
  comment: string | null;
  createdAt: string;
}

@Injectable({providedIn: 'root'})
export class MovementService {
  private http = inject(HttpClient);

  getRecent(): Observable<Movement[]> {
    return this.http.get<Movement[]>('/api/v1/stock-movements');
  }
}
