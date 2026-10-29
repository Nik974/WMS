import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { Customer, CustomerRequest, CustomerService } from './customer.service';

@Component({
  selector: 'app-customer-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ customer ? 'Edit customer' : 'New customer' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="customer-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Name</mat-label>
          <input matInput formControlName="customerName" />
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="customer-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(320px, 100%); padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class CustomerDialog {
  private fb = inject(FormBuilder);
  private service = inject(CustomerService);
  private ref = inject(MatDialogRef<CustomerDialog>);
  customer: Customer | null = inject(MAT_DIALOG_DATA, { optional: true }) ?? null;

  saving = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    customerName: [this.customer?.customerName ?? '', Validators.required],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const req: CustomerRequest = { customerName: this.form.getRawValue().customerName.trim() };

    const call = this.customer
      ? this.service.update(this.customer.customerId, req)
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