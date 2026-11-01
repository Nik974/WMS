import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { Supplier, SupplierRequest, SupplierService } from './supplier.service';

@Component({
  selector: 'app-supplier-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ supplier ? 'Edit supplier' : 'New supplier' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="supplier-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Name</mat-label>
          <input matInput formControlName="supplierName" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Tax ID</mat-label>
          <input matInput formControlName="taxId" />
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="supplier-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(320px, 100%); padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class SupplierDialog {
  private fb = inject(FormBuilder);
  private service = inject(SupplierService);
  private ref = inject(MatDialogRef<SupplierDialog>);
  supplier: Supplier | null = inject(MAT_DIALOG_DATA, { optional: true }) ?? null;

  saving = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    supplierName: [this.supplier?.supplierName ?? '', Validators.required],
    taxId: [this.supplier?.taxId ?? '', Validators.required],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: SupplierRequest = {
      supplierName: v.supplierName.trim(),
      taxId: v.taxId.trim(),
    };

    const call = this.supplier
      ? this.service.update(this.supplier.supplierId, req)
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
