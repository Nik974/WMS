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
import { ProductService, Product } from '../products/product.service';
import { SupplierService, Supplier } from '../suppliers/supplier.service';
import { Batch, BatchService } from './batch.service';
import { BatchDialog } from './batch-dialog';

type ExpiryState = 'none' | 'expired' | 'soon' | 'ok';
const SOON_DAYS = 30;

@Component({
  selector: 'app-batches',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './batches.html',
  styleUrls: ['./batches.scss'],
})
export class Batches implements OnInit, AfterViewInit {
  private service = inject(BatchService);
  private productService = inject(ProductService);
  private supplierService = inject(SupplierService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['lotNumber', 'product', 'expiryDate', 'supplier', 'actions'];
  dataSource = new MatTableDataSource<Batch>([]);
  loading = signal(true);
  products = signal<Product[]>([]);
  suppliers = signal<Supplier[]>([]);

  private productNames = computed(() => new Map(this.products().map((p) => [p.productId, p.productName])));
  private supplierNames = computed(() => new Map(this.suppliers().map((s) => [s.supplierId, s.supplierName])));

  filterForm = this.fb.nonNullable.group({
    lot: '',
    productId: null as number | null,
    expiry: '' as '' | 'expired' | 'soon',
  });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (b, raw) => {
      const f = JSON.parse(raw) as { lot: string; productId: number | null; expiry: string };
      const okLot = !f.lot || b.lotNumber.toLowerCase().includes(f.lot.toLowerCase());
      const okProduct = f.productId == null || b.productId === f.productId;
      const okExpiry = !f.expiry || this.expiryState(b) === f.expiry;
      return okLot && okProduct && okExpiry;
    };
    this.filterForm.valueChanges.subscribe((v) => {
      this.dataSource.filter = JSON.stringify(v);
      this.paginator?.firstPage();
    });
  }

  ngOnInit() {
    this.productService.getAll().subscribe((l) => this.products.set(l ?? []));
    this.supplierService.getAll().subscribe((l) => this.suppliers.set(l ?? []));
    this.load();
  }
  ngAfterViewInit() { this.dataSource.paginator = this.paginator; }

  load() {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (list) => { this.dataSource.data = list ?? []; this.loading.set(false); },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load batches', 'OK', { duration: 4000 });
      },
    });
  }

  productName(id: number) { return this.productNames().get(id) ?? `#${id}`; }
  supplierName(id: number | null) { return id == null ? '—' : (this.supplierNames().get(id) ?? `#${id}`); }

  expiryState(b: Batch): ExpiryState {
    if (!b.expiryDate) return 'none';
    const expiry = new Date(b.expiryDate + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const days = Math.floor((expiry.getTime() - today.getTime()) / 86_400_000);
    if (days < 0) return 'expired';
    if (days <= SOON_DAYS) return 'soon';
    return 'ok';
  }

  clearFilters() { this.filterForm.reset(); }

  openDialog(batch?: Batch) {
    this.dialog.open(BatchDialog, { data: batch ?? null, width: '440px', maxWidth: '95vw' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open(batch ? 'Batch updated' : 'Batch created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }
}
