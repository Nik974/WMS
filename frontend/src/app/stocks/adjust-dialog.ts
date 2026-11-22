import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {HttpErrorResponse} from '@angular/common/http';
import {AdjustRequest, Stock, StockService} from './stock.service';
import {Warehouse, WarehouseService} from '../warehouses/warehouse.service';
import {Location, LocationService} from '../locations/location.service';
import {Product, ProductService} from '../products/product.service';
import {Batch, BatchService} from '../batches/batch.service';

@Component({
  selector: 'app-adjust-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ stock ? 'Adjust stock' : 'Add stock' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="adjust-form" (ngSubmit)="save()" class="form">
        @if (stock) {
          <p class="info">
            {{ stock.productName }} · lot {{ stock.lotNumber }} · {{ stock.locationCode }}<br/>
            Current: {{ stock.quantity }}, reserved: {{ stock.reservedQuantity }}
          </p>
        } @else {
          <mat-form-field appearance="outline">
            <mat-label>Warehouse</mat-label>
            <mat-select formControlName="warehouseId" (selectionChange)="onWarehouse($event.value)">
              @for (w of warehouses(); track w.id) {
                <mat-option [value]="w.id">{{ w.warehouseName }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Location</mat-label>
            <mat-select formControlName="locationId">
              @for (l of locations(); track l.locationId) {
                <mat-option [value]="l.locationId">{{ l.code }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Product</mat-label>
            <mat-select formControlName="productId" (selectionChange)="form.controls.batchId.reset()">
              @for (p of products(); track p.productId) {
                <mat-option [value]="p.productId">{{ p.productName }} ({{ p.productSku }})</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Batch</mat-label>
            <mat-select formControlName="batchId">
              @for (b of batchesForProduct(); track b.batchId) {
                <mat-option [value]="b.batchId">{{ b.lotNumber }}{{ b.expiryDate ? ' (' + b.expiryDate + ')' : '' }}
                </mat-option>
              } @empty {
                <mat-option disabled>Select a product with batches</mat-option>
              }
            </mat-select>
          </mat-form-field>
        }

        <mat-form-field appearance="outline">
          <mat-label>New quantity</mat-label>
          <input matInput type="number" min="0" formControlName="newQuantity"/>
          @if (form.controls.newQuantity.hasError('min')) {
            <mat-error>Cannot be negative</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Reason</mat-label>
          <input matInput formControlName="reason" placeholder="Inventory count, damage, initial stock..."/>
        </mat-form-field>

        @if (error()) {
          <p class="error">{{ error() }}</p>
        }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="adjust-form" [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form {
      display: flex;
      flex-direction: column;
      min-width: min(400px, 100%);
      padding-top: 8px;
    }

    .info {
      margin: 0 0 16px;
      color: var(--mat-sys-on-surface-variant);
    }

    .error {
      color: var(--mat-sys-error);
      margin: 0;
    }
  `,
})
export class AdjustDialog {
  private fb = inject(FormBuilder);
  private service = inject(StockService);
  private locationService = inject(LocationService);
  private ref = inject(MatDialogRef<AdjustDialog>);
  stock: Stock | null = inject(MAT_DIALOG_DATA, {optional: true}) ?? null;

  warehouses = signal<Warehouse[]>([]);
  locations = signal<Location[]>([]);
  products = signal<Product[]>([]);
  batches = signal<Batch[]>([]);
  saving = signal(false);
  error = signal('');

  form = this.fb.group({
    warehouseId: [null as number | null],
    locationId: [this.stock?.locationId ?? (null as number | null), Validators.required],
    productId: [this.stock?.productId ?? (null as number | null), Validators.required],
    batchId: [this.stock?.batchId ?? (null as number | null), Validators.required],
    newQuantity: [this.stock?.quantity ?? (null as number | null), [Validators.required, Validators.min(0)]],
    reason: ['', Validators.required],
  });

  constructor() {
    if (!this.stock) {
      inject(WarehouseService).getAll().subscribe((l) => this.warehouses.set(l ?? []));
      inject(ProductService).getAll().subscribe((l) => this.products.set(l ?? []));
      inject(BatchService).getAll().subscribe((l) => this.batches.set(l ?? []));
    }
  }

  batchesForProduct() {
    const id = this.form.controls.productId.value;
    return this.batches().filter((b) => b.productId === id);
  }

  onWarehouse(id: number) {
    this.form.controls.locationId.reset();
    this.locationService.getByWarehouse(id).subscribe((l) => this.locations.set(l ?? []));
  }

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: AdjustRequest = {
      locationId: v.locationId!,
      productId: v.productId!,
      batchId: v.batchId!,
      newQuantity: v.newQuantity!,
      reason: (v.reason ?? '').trim(),
    };

    this.service.adjust(req).subscribe({
      next: (saved) => this.ref.close(saved),
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Failed to save');
      },
    });
  }
}
