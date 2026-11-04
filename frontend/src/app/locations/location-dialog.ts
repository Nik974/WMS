import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { Location, LocationRequest, LocationService } from './location.service';

export interface LocationDialogData {
  warehouseId: number;
  location: Location | null;
}

@Component({
  selector: 'app-location-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ data.location ? 'Edit location' : 'New location' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="location-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Code</mat-label>
          <input matInput formControlName="code" />
        </mat-form-field>

        <div class="row">
          <mat-form-field appearance="outline">
            <mat-label>Zone</mat-label>
            <input matInput formControlName="zone" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Aisle</mat-label>
            <input matInput formControlName="aisle" />
          </mat-form-field>
        </div>

        <div class="row">
          <mat-form-field appearance="outline">
            <mat-label>Rack</mat-label>
            <input matInput formControlName="rack" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Shelf</mat-label>
            <input matInput formControlName="shelf" />
          </mat-form-field>
        </div>

        <div class="row">
          <mat-form-field appearance="outline">
            <mat-label>Bin</mat-label>
            <input matInput formControlName="bin" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Capacity</mat-label>
            <input matInput type="number" min="0" formControlName="capacity" />
          </mat-form-field>
        </div>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="location-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(420px, 100%); padding-top: 8px; }
    .row { display: flex; gap: 12px; }
    .row mat-form-field { flex: 1; min-width: 0; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class LocationDialog {
  private fb = inject(FormBuilder);
  private service = inject(LocationService);
  private ref = inject(MatDialogRef<LocationDialog>);
  data: LocationDialogData = inject(MAT_DIALOG_DATA);

  saving = signal(false);
  error = signal('');

  private l = this.data.location;
  form = this.fb.group({
    code: [this.l?.code ?? '', Validators.required],
    zone: [this.l?.zone ?? ''],
    aisle: [this.l?.aisle ?? ''],
    rack: [this.l?.rack ?? ''],
    shelf: [this.l?.shelf ?? ''],
    bin: [this.l?.bin ?? ''],
    capacity: [this.l?.capacity ?? (null as number | null), Validators.min(0)],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const clean = (s: string | null) => (s?.trim() ? s.trim() : null);
    const req: LocationRequest = {
      warehouseId: this.data.location?.warehouseId ?? this.data.warehouseId,
      code: clean(v.code),
      zone: clean(v.zone),
      aisle: clean(v.aisle),
      rack: clean(v.rack),
      shelf: clean(v.shelf),
      bin: clean(v.bin),
      capacity: v.capacity ?? null,
    };

    const call = this.data.location
      ? this.service.update(this.data.location.locationId, req)
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
