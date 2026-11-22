import {AfterViewInit, Component, computed, inject, OnInit, signal, ViewChild} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Header} from '../shared/header/header';
import {AuthService} from '../auth/auth.service';
import {Warehouse, WarehouseService} from '../warehouses/warehouse.service';
import {Stock, StockService} from './stock.service';
import {AdjustDialog} from './adjust-dialog';

type ExpiryState = 'none' | 'expired' | 'soon' | 'ok';
const SOON_DAYS = 30;

@Component({
  selector: 'app-stocks',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './stocks.html',
  styleUrls: ['./stocks.scss'],
})
export class Stocks implements OnInit, AfterViewInit {
  private service = inject(StockService);
  private warehouseService = inject(WarehouseService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['product', 'lot', 'location', 'expiry', 'quantity', 'reserved', 'available', 'actions'];
  dataSource = new MatTableDataSource<Stock>([]);
  loading = signal(true);
  warehouses = signal<Warehouse[]>([]);

  filterForm = this.fb.nonNullable.group({
    warehouseId: null as number | null,
    text: '',
    onlyAvailable: false,
  });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (s, raw) => {
      const f = JSON.parse(raw) as { text: string; onlyAvailable: boolean };
      const t = f.text.toLowerCase();
      const okText = !t || [s.productName, s.productSku, s.lotNumber, s.locationCode]
        .some((v) => (v ?? '').toLowerCase().includes(t));
      return okText && (!f.onlyAvailable || s.available > 0);
    };

    let lastWarehouse: number | null = null;
    this.filterForm.valueChanges.subscribe((v) => {
      this.dataSource.filter = JSON.stringify({text: v.text ?? '', onlyAvailable: !!v.onlyAvailable});
      this.paginator?.firstPage();
      if ((v.warehouseId ?? null) !== lastWarehouse) {
        lastWarehouse = v.warehouseId ?? null;
        this.load();   // склад фильтруется на сервере
      }
    });
  }

  ngOnInit() {
    this.warehouseService.getAll().subscribe((l) => this.warehouses.set(l ?? []));
    this.load();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  load() {
    this.loading.set(true);
    this.service.getAll(this.filterForm.controls.warehouseId.value).subscribe({
      next: (list) => {
        this.dataSource.data = list ?? [];
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load stock', 'OK', {duration: 4000});
      },
    });
  }

  expiryState(s: Stock): ExpiryState {
    if (!s.expiryDate) return 'none';
    const expiry = new Date(s.expiryDate + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const days = Math.floor((expiry.getTime() - today.getTime()) / 86_400_000);
    if (days < 0) return 'expired';
    return days <= SOON_DAYS ? 'soon' : 'ok';
  }

  clearFilters() {
    this.filterForm.reset();
  }

  openAdjust(stock?: Stock) {
    this.dialog.open(AdjustDialog, {data: stock ?? null, width: '460px', maxWidth: '95vw'})
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open('Stock updated', 'OK', {duration: 3000});
        this.load();
      }
    });
  }
}
