import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import {Product, ProductRequest, ProductService} from './product.service';
import {Category, CategoryService} from '../categories/category.service';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';

@Component({
  selector: 'app-product-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatOption, MatSelect],
  template: `
    <h2 mat-dialog-title>{{ product ? 'Edit product' : 'New product' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="product-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>SKU</mat-label>
          <input matInput formControlName="productSku" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Name</mat-label>
          <input matInput formControlName="productName" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Unit</mat-label>
          <input matInput formControlName="unit" placeholder="pcs, kg, box..." />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Category</mat-label>
          <mat-select formControlName="categoryId">
            @for (c of categories(); track c.categoryId) {
              <mat-option [value]="c.categoryId">{{ c.categoryName }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="product-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; gap: 4px; min-width: 320px; padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class ProductDialog {
  private fb = inject(FormBuilder);
  private service = inject(ProductService);
  private ref = inject(MatDialogRef<ProductDialog>);
  private categoryService = inject(CategoryService);
  categories = signal<Category[]>([]);

  constructor() {
    this.categoryService.getAll().subscribe((list) => this.categories.set(list));
  }
  product: Product | null = inject(MAT_DIALOG_DATA, { optional: true }) ?? null;

  saving = signal(false);
  error = signal('');

  form = this.fb.group({
    productSku: [this.product?.productSku ?? '', Validators.required],
    productName: [this.product?.productName ?? '', Validators.required],
    unit: [this.product?.unit ?? ''],
    categoryId: [this.product?.categoryId ?? (null as number | null), Validators.required],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: ProductRequest = {
      productSku: v.productSku!,
      productName: v.productName!,
      unit: v.unit?.trim() ? v.unit.trim() : null,
      categoryId: v.categoryId!,
    };
    const call = this.product
      ? this.service.update(this.product.productId, req)
      : this.service.create(req);

    call.subscribe({
      next: (saved) => this.ref.close(saved),
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Failed to save');
      },
    });
  }
}
