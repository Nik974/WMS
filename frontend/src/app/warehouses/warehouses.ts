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
import { Warehouse, WarehouseService } from './warehouse.service';
import { WarehouseDialog } from './warehouse-dialog';

@Component({
  selector: 'app-warehouses',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './warehouses.html',
  styleUrls: ['./warehouses.scss'],
})
export class Warehouses implements OnInit, AfterViewInit {
  private service = inject(WarehouseService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['warehouseName', 'warehouseAddress', 'actions'];
  dataSource = new MatTableDataSource<Warehouse>([]);
  loading = signal(true);

  filterForm = this.fb.nonNullable.group({ name: '', address: '' });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (w, raw) => {
      const f = JSON.parse(raw) as { name: string; address: string };
      const has = (value: string | null | undefined, needle: string) =>
        !needle || (value ?? '').toLowerCase().includes(needle.toLowerCase());
      return has(w.warehouseName, f.name) && has(w.warehouseAddress, f.address);
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
      next: (list) => { this.dataSource.data = list; this.loading.set(false); },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load warehouses', 'OK', { duration: 4000 });
      },
    });
  }

  clearFilters() { this.filterForm.reset(); }

  openDialog(warehouse?: Warehouse) {
    this.dialog.open(WarehouseDialog, { data: warehouse ?? null, width: '420px' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open(warehouse ? 'Warehouse updated' : 'Warehouse created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }
}
