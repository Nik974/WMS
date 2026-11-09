import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { AdminUserService, AppUser, ROLES, UpdateUserRequest } from './admin-user.service';

export interface UserDialogData {
  user: AppUser;
  isSelf: boolean;
}

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Edit user</h2>

    <mat-dialog-content>
      <form [formGroup]="form" id="user-form" (ngSubmit)="save()" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Username</mat-label>
          <input matInput formControlName="username" />
          @if (data.isSelf) { <mat-hint>You cannot change your own username</mat-hint> }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email" />
          @if (form.controls.email.hasError('email')) { <mat-error>Invalid email</mat-error> }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Role</mat-label>
          <mat-select formControlName="role">
            @for (r of roles; track r) {
              <mat-option [value]="r">{{ r }}</mat-option>
            }
          </mat-select>
          @if (data.isSelf) { <mat-hint>You cannot change your own role</mat-hint> }
        </mat-form-field>

        @if (error()) { <p class="error">{{ error() }}</p> }
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-flat-button type="submit" form="user-form"
              [disabled]="form.invalid || saving()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form { display: flex; flex-direction: column; min-width: min(380px, 100%); padding-top: 8px; }
    .error { color: var(--mat-sys-error); margin: 0; }
  `,
})
export class UserDialog {
  private fb = inject(FormBuilder);
  private service = inject(AdminUserService);
  private ref = inject(MatDialogRef<UserDialog>);
  data: UserDialogData = inject(MAT_DIALOG_DATA);

  roles = ROLES;
  saving = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    username: [
      { value: this.data.user.username, disabled: this.data.isSelf },
      [Validators.required, Validators.minLength(3)],
    ],
    email: [this.data.user.email, [Validators.required, Validators.email]],
    role: [
      { value: this.data.user.role.toUpperCase(), disabled: this.data.isSelf },
      Validators.required,
    ],
  });

  save() {
    if (this.form.invalid || this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const v = this.form.getRawValue();
    const req: UpdateUserRequest = {
      username: v.username.trim(),
      email: v.email.trim(),
      role: v.role,
    };

    this.service.update(this.data.user.id, req).subscribe({
      next: (saved) => this.ref.close(saved),
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Failed to save');
      },
    });
  }
}
