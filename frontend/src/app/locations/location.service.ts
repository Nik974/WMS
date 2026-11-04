import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Location {
  locationId: number;
  warehouseId: number;
  zone: string | null;
  aisle: string | null;
  rack: string | null;
  shelf: string | null;
  bin: string | null;
  capacity: number | null;
  code: string | null;
}

export interface LocationRequest {
  warehouseId: number;
  zone: string | null;
  aisle: string | null;
  rack: string | null;
  shelf: string | null;
  bin: string | null;
  capacity: number | null;
  code: string | null;
}

@Injectable({ providedIn: 'root' })
export class LocationService {
  private http = inject(HttpClient);
  private url = '/api/v1/locations';

  getByWarehouse(warehouseId: number): Observable<Location[]> {
    return this.http.get<Location[]>(this.url, { params: { warehouseId } });
  }

  create(req: LocationRequest): Observable<Location> {
    return this.http.post<Location>(this.url, req);
  }

  update(id: number, req: LocationRequest): Observable<Location> {
    return this.http.put<Location>(`${this.url}/${id}`, req);
  }
}
