import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { Category, CategoryService } from './category.service';

@Component({
  selector: 'app-category-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ category ? 'Edit category' : 'New category' }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="category-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Name</mat-label>
          <input matInput formControlName="categoryName" />
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="category-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: 320px; padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class CategoryDialog {
  private fb = inject(FormBuilder);
  private service = inject(CategoryService);
  private ref = inject(MatDialogRef<CategoryDialog>);
  category: Category | null = inject(MAT_DIALOG_DATA, { optional: true }) ?? null;

  saving = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    categoryName: [this.category?.categoryName ?? '', Validators.required],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const req = { categoryName: this.form.getRawValue().categoryName.trim() };
    const call = this.category
      ? this.service.update(this.category.categoryId, req)
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
