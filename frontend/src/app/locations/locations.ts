import { AfterViewInit, Component, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Header } from '../shared/header/header';
import { AuthService } from '../auth/auth.service';
import { Warehouse, WarehouseService } from '../warehouses/warehouse.service';
import { Location, LocationService } from './location.service';
import { LocationDialog } from './location-dialog';

@Component({
  selector: 'app-locations',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './locations.html',
  styleUrls: ['./locations.scss'],
})
export class Locations implements OnInit, AfterViewInit {
  private service = inject(LocationService);
  private warehouseService = inject(WarehouseService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['code', 'zone', 'aisle', 'rack', 'shelf', 'bin', 'capacity', 'actions'];
  dataSource = new MatTableDataSource<Location>([]);
  warehouses = signal<Warehouse[]>([]);
  warehouseId = signal<number | null>(null);
  loading = signal(false);
  search = new FormControl('', { nonNullable: true });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (l, needle) =>
      [l.code, l.zone, l.aisle, l.rack, l.shelf, l.bin]
        .some((v) => (v ?? '').toLowerCase().includes(needle.toLowerCase()));

    this.search.valueChanges.subscribe((v) => {
      this.dataSource.filter = v.trim();
      this.paginator?.firstPage();
    });
  }

  ngOnInit() {
    this.warehouseService.getAll().subscribe({
      next: (list) => {
        this.warehouses.set(list ?? []);
        if (list?.length) this.selectWarehouse(list[0].id);
      },
      error: () => this.snack.open('Failed to load warehouses', 'OK', { duration: 4000 }),
    });
  }

  ngAfterViewInit() { this.dataSource.paginator = this.paginator; }

  selectWarehouse(id: number) {
    this.warehouseId.set(id);
    this.search.reset('');
    this.load();
  }

  load() {
    const id = this.warehouseId();
    if (id === null) return;
    this.loading.set(true);
    this.service.getByWarehouse(id).subscribe({
      next: (list) => { this.dataSource.data = list ?? []; this.loading.set(false); },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load locations', 'OK', { duration: 4000 });
      },
    });
  }

  openDialog(location?: Location) {
    const id = this.warehouseId();
    if (id === null) return;
    this.dialog.open(LocationDialog, {
      data: { warehouseId: id, location: location ?? null },
      width: '480px',
      maxWidth: '95vw',
    }).afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open(location ? 'Location updated' : 'Location created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }
}
