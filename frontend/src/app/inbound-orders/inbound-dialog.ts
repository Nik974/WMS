import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { HttpErrorResponse } from '@angular/common/http';
import { CreateInboundRequest, InboundService } from './inbound.service';
import { Supplier, SupplierService } from '../suppliers/supplier.service';
import { Warehouse, WarehouseService } from '../warehouses/warehouse.service';
import { Product, ProductService } from '../products/product.service';

@Component({
  selector: 'app-inbound-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatIconModule,
  ],
  template: `
    <h2 mat-dialog-title>New inbound order</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="inbound-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Supplier</mat-label>
          <mat-select formControlName="supplierId">
            @for (s of suppliers(); track s.supplierId) {
              <mat-option [value]="s.supplierId">{{ s.supplierName }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Warehouse</mat-label>
          <mat-select formControlName="warehouseId">
            @for (w of warehouses(); track w.id) {
              <mat-option [value]="w.id">{{ w.warehouseName }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <h3>Items</h3>
        <div formArrayName="items">
          @for (item of items.controls; track item; let i = $index) {
            <div [formGroupName]="i" class="item-row">
              <mat-form-field appearance="outline" class="product">
                <mat-label>Product</mat-label>
                <mat-select formControlName="productId">
                  @for (p of products(); track p.productId) {
                    <mat-option [value]="p.productId">{{ p.productName }} ({{ p.productSku }})</mat-option>
                  }
                </mat-select>
              </mat-form-field>

              <mat-form-field appearance="outline" class="qty">
                <mat-label>Qty</mat-label>
                <input matInput type="number" min="1" formControlName="quantity" />
              </mat-form-field>

              <button mat-icon-button type="button" (click)="removeItem(i)"
                      [disabled]="items.length === 1" aria-label="Remove item">
                <mat-icon>delete</mat-icon>
              </button>
            </div>
          }
        </div>
        <button mat-button type="button" (click)="addItem()">
          <mat-icon>add</mat-icon> Add item
        </button>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="inbound-form"
              [disabled]="form.invalid || saving()">Create</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(480px, 100%); padding-top: 8px; }
    h3 { margin: 8px 0; font: var(--mat-sys-title-medium); }
    .item-row { display: flex; gap: 8px; align-items: flex-start; }
    .product { flex: 1; min-width: 0; }
    .qty { width: 90px; }
    .error { color: var(--mat-sys-error); margin: 8px 0 0; }
  `,
})
export class InboundDialog {
  private fb = inject(FormBuilder);
  private service = inject(InboundService);
  private ref = inject(MatDialogRef<InboundDialog>);

  suppliers = signal<Supplier[]>([]);
  warehouses = signal<Warehouse[]>([]);
  products = signal<Product[]>([]);
  saving = signal(false);
  error = signal('');

  form = this.fb.group({
    supplierId: [null as number | null, Validators.required],
    warehouseId: [null as number | null, Validators.required],
    items: this.fb.array([this.newItem()]),
  });

  constructor() {
    inject(SupplierService).getAll().subscribe((l) => this.suppliers.set(l ?? []));
    inject(WarehouseService).getAll().subscribe((l) => this.warehouses.set(l ?? []));
    inject(ProductService).getAll().subscribe((l) => this.products.set(l ?? []));
  }

  get items(): FormArray {
    return this.form.controls.items;
  }

  private newItem() {
    return this.fb.group({
      productId: [null as number | null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
    });
  }

  addItem() { this.items.push(this.newItem()); }
  removeItem(i: number) { if (this.items.length > 1) this.items.removeAt(i); }

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: CreateInboundRequest = {
      supplierId: v.supplierId!,
      warehouseId: v.warehouseId!,
      items: v.items.map((i: any) => ({ productId: i.productId, quantity: i.quantity })),
    };

    this.service.create(req).subscribe({
      next: (saved) => this.ref.close(saved),
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Failed to create order');
      },
    });
  }
}
