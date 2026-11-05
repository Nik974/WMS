import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { Batch, BatchRequest, BatchService } from './batch.service';
import { Product, ProductService } from '../products/product.service';
import { Supplier, SupplierService } from '../suppliers/supplier.service';

@Component({
  selector: 'app-batch-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>{{ batch ? 'Edit batch' : 'New batch' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="batch-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Product</mat-label>
          <mat-select formControlName="productId">
            @for (p of products(); track p.productId) {
              <mat-option [value]="p.productId">{{ p.productName }} ({{ p.productSku }})</mat-option>
            }
          </mat-select>
          @if (batch) { <mat-hint>Product cannot be changed</mat-hint> }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Lot number</mat-label>
          <input matInput formControlName="lotNumber" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Expiry date</mat-label>
          <input matInput type="date" formControlName="expiryDate" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Supplier</mat-label>
          <mat-select formControlName="supplierId">
            <mat-option [value]="null">— none —</mat-option>
            @for (s of suppliers(); track s.supplierId) {
              <mat-option [value]="s.supplierId">{{ s.supplierName }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="batch-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(380px, 100%); padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class BatchDialog {
  private fb = inject(FormBuilder);
  private service = inject(BatchService);
  private ref = inject(MatDialogRef<BatchDialog>);
  batch: Batch | null = inject(MAT_DIALOG_DATA, { optional: true }) ?? null;

  products = signal<Product[]>([]);
  suppliers = signal<Supplier[]>([]);
  saving = signal(false);
  error = signal('');

  form = this.fb.group({
    productId: [
      { value: this.batch?.productId ?? (null as number | null), disabled: !!this.batch },
      Validators.required,
    ],
    lotNumber: [this.batch?.lotNumber ?? '', Validators.required],
    expiryDate: [this.batch?.expiryDate ?? ''],
    supplierId: [this.batch?.supplierId ?? (null as number | null)],
  });

  constructor() {
    inject(ProductService).getAll().subscribe((l) => this.products.set(l ?? []));
    inject(SupplierService).getAll().subscribe((l) => this.suppliers.set(l ?? []));
  }

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: BatchRequest = {
      productId: v.productId!,
      lotNumber: (v.lotNumber ?? '').trim(),
      expiryDate: v.expiryDate || null,
      supplierId: v.supplierId ?? null,
    };

    const call = this.batch
      ? this.service.update(this.batch.batchId, req)
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
