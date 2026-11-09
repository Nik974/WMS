import { AfterViewInit, Component, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Header } from '../shared/header/header';
import { AuthService } from '../auth/auth.service';
import { AdminUserService, AppUser, ROLES } from './admin-user.service';
import { UserDialog } from './user-dialog';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './users.html',
  styleUrls: ['./users.scss'],
})
export class Users implements OnInit, AfterViewInit {
  private service = inject(AdminUserService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['username', 'email', 'role', 'status', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<AppUser>([]);
  loading = signal(true);
  roles = ROLES;

  myUsername = computed(() => this.auth.session()?.username ?? '');

  filterForm = this.fb.nonNullable.group({ text: '', role: '' });

  constructor() {
    this.dataSource.filterPredicate = (u, raw) => {
      const f = JSON.parse(raw) as { text: string; role: string };
      const t = f.text.toLowerCase();
      const okText = !t || u.username.toLowerCase().includes(t) || u.email.toLowerCase().includes(t);
      const okRole = !f.role || u.role.toUpperCase() === f.role;
      return okText && okRole;
    };
    this.filterForm.valueChanges.subscribe((v) => {
      this.dataSource.filter = JSON.stringify(v);
      this.paginator?.firstPage();
    });
  }

  ngOnInit() { this.load(); }
  ngAfterViewInit() { this.dataSource.paginator = this.paginator; }

  load() {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (list) => { this.dataSource.data = list ?? []; this.loading.set(false); },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load users', 'OK', { duration: 4000 });
      },
    });
  }

  isSelf(u: AppUser): boolean { return u.username === this.myUsername(); }
  clearFilters() { this.filterForm.reset(); }

  openEdit(user: AppUser) {
    this.dialog.open(UserDialog, {
      data: { user, isSelf: this.isSelf(user) },
      width: '440px',
      maxWidth: '95vw',
    }).afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open('User updated', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }

  toggleBlocked(user: AppUser) {
    const block = user.enabled;
    if (block && !confirm(`Block user "${user.username}"?`)) return;

    this.service.setBlocked(user.id, block).subscribe({
      next: () => {
        this.snack.open(block ? 'User blocked' : 'User unblocked', 'OK', { duration: 3000 });
        this.load();
      },
      error: (err) =>
        this.snack.open(err.error?.message ?? 'Operation failed', 'OK', { duration: 5000 }),
    });
  }
}
