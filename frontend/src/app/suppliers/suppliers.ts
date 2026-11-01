import { AfterViewInit, Component, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Header } from '../shared/header/header';
import { AuthService } from '../auth/auth.service';
import { Supplier, SupplierService } from './supplier.service';
import { SupplierDialog } from './supplier-dialog';

@Component({
  selector: 'app-suppliers',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './suppliers.html',
  styleUrls: ['./suppliers.scss'],
})
export class Suppliers implements OnInit, AfterViewInit {
  private service = inject(SupplierService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['supplierName', 'taxId', 'actions'];
  dataSource = new MatTableDataSource<Supplier>([]);
  loading = signal(true);

  filterForm = this.fb.nonNullable.group({ name: '', taxId: '' });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (s, raw) => {
      const f = JSON.parse(raw) as { name: string; taxId: string };
      const has = (value: string | null | undefined, needle: string) =>
        !needle || (value ?? '').toLowerCase().includes(needle.toLowerCase());
      return has(s.supplierName, f.name) && has(s.taxId, f.taxId);
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
        this.snack.open('Failed to load suppliers', 'OK', { duration: 4000 });
      },
    });
  }

  clearFilters() { this.filterForm.reset(); }

  openDialog(supplier?: Supplier) {
    this.dialog.open(SupplierDialog, { data: supplier ?? null, width: '420px', maxWidth: '95vw' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open(supplier ? 'Supplier updated' : 'Supplier created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }
}
