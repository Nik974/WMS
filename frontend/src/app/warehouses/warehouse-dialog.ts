import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { Warehouse, WarehouseRequest, WarehouseService } from './warehouse.service';

@Component({
  selector: 'app-warehouse-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ warehouse ? 'Edit warehouse' : 'New warehouse' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="warehouse-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Name</mat-label>
          <input matInput formControlName="warehouseName" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Address</mat-label>
          <input matInput formControlName="warehouseAddress" />
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="warehouse-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(320px, 100%); padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class WarehouseDialog {
  private fb = inject(FormBuilder);
  private service = inject(WarehouseService);
  private ref = inject(MatDialogRef<WarehouseDialog>);
  warehouse: Warehouse | null = inject(MAT_DIALOG_DATA, { optional: true }) ?? null;

  saving = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    warehouseName: [this.warehouse?.warehouseName ?? '', Validators.required],
    warehouseAddress: [this.warehouse?.warehouseAddress ?? ''],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: WarehouseRequest = {
      warehouseName: v.warehouseName.trim(),
      warehouseAddress: v.warehouseAddress.trim() || null,
    };

    const call = this.warehouse
      ? this.service.update(this.warehouse.id, req)
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
