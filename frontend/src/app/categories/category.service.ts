import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Category {
  categoryId: number;
  categoryName: string;
}

export interface CategoryRequest {
  categoryName: string;
}

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private http = inject(HttpClient);
  private url = '/api/v1/categories';

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(this.url);
  }

  create(req: CategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.url, req);
  }

  update(id: number, req: CategoryRequest): Observable<Category> {
    return this.http.put<Category>(`${this.url}/${id}`, req);
  }
}
