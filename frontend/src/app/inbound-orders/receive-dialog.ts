import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {HttpErrorResponse} from '@angular/common/http';
import {InboundOrder, InboundService} from './inbound.service';
import {Location, LocationService} from '../locations/location.service';
import {Batch, BatchService} from '../batches/batch.service';

@Component({
  selector: 'app-receive-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatSelectModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Receive order #{{ order.orderId }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="receive-form" (ngSubmit)="save()" class="form">
        @for (item of order.items; track item.id; let i = $index) {
          <div [formGroup]="lineGroup(i)" class="line">
            <div class="title">{{ item.productName ?? ('#' + item.productId) }} × {{ item.quantity }}</div>

            <mat-form-field appearance="outline">
              <mat-label>Location</mat-label>
              <mat-select formControlName="locationId">
                @for (l of locations(); track l.locationId) {
                  <mat-option [value]="l.locationId">{{ l.code }}</mat-option>
                }
              </mat-select>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Batch</mat-label>
              <mat-select formControlName="batchId">
                @for (b of batchesFor(item.productId); track b.batchId) {
                  <mat-option [value]="b.batchId">{{ b.lotNumber }}{{ b.expiryDate ? ' (' + b.expiryDate + ')' : '' }}
                  </mat-option>
                } @empty {
                  <mat-option disabled>No batches for this product</mat-option>
                }
              </mat-select>
            </mat-form-field>
          </div>
        }
        @if (error()) {
          <p class="error">{{ error() }}</p>
        }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="receive-form"
              [disabled]="form.invalid || saving()">Receive
      </button>
    </mat-dialog-actions>
  `,
  styles: `
    .form {
      display: flex;
      flex-direction: column;
      gap: 8px;
      min-width: min(460px, 100%);
      padding-top: 8px;
    }

    .line {
      display: flex;
      flex-direction: column;
      border-bottom: 1px solid var(--mat-sys-outline-variant);
      padding-bottom: 8px;
    }

    .title {
      font: var(--mat-sys-title-small);
      margin-bottom: 8px;
    }

    .error {
      color: var(--mat-sys-error);
      margin: 0;
    }
  `,
})
export class ReceiveDialog {
  private fb = inject(FormBuilder);
  private service = inject(InboundService);
  private ref = inject(MatDialogRef<ReceiveDialog>);
  order: InboundOrder = inject(MAT_DIALOG_DATA);

  locations = signal<Location[]>([]);
  batches = signal<Batch[]>([]);
  saving = signal(false);
  error = signal('');

  form = this.fb.group({
    lines: this.fb.array(
      this.order.items.map(() =>
        this.fb.group({
          locationId: [null as number | null, Validators.required],
          batchId: [null as number | null, Validators.required],
        }),
      ),
    ),
  });

  constructor() {
    inject(LocationService).getByWarehouse(this.order.warehouseId)
      .subscribe((l) => this.locations.set(l ?? []));
    inject(BatchService).getAll().subscribe((l) => this.batches.set(l ?? []));
  }

  lineGroup(i: number) {
    return this.form.controls.lines.at(i);
  }

  batchesFor(productId: number) {
    return this.batches().filter((b) => b.productId === productId);
  }

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const raw = this.form.getRawValue().lines;
    const lines = this.order.items.map((item, i) => ({
      itemId: item.id,
      locationId: raw[i].locationId!,
      batchId: raw[i].batchId!,
    }));

    this.service.receive(this.order.orderId, lines).subscribe({
      next: (saved) => this.ref.close(saved),
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Failed to receive order');
      },
    });
  }
}
