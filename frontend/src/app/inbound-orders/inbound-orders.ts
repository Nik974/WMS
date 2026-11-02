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
import { MatMenuModule } from '@angular/material/menu';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Header } from '../shared/header/header';
import { AuthService } from '../auth/auth.service';
import { WarehouseService } from '../warehouses/warehouse.service';
import {
  INBOUND_LABELS, INBOUND_TRANSITIONS, InboundOrder, InboundService, InboundStatus,
} from './inbound.service';
import { InboundDialog } from './inbound-dialog';

@Component({
  selector: 'app-inbound-orders',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
    MatMenuModule, Header,
  ],
  templateUrl: './inbound-orders.html',
  styleUrls: ['./inbound-orders.scss'],
})
export class InboundOrders implements OnInit, AfterViewInit {
  private service = inject(InboundService);
  private warehouseService = inject(WarehouseService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['orderId', 'supplierName', 'warehouse', 'status', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<InboundOrder>([]);
  loading = signal(true);
  expandedId = signal<number | null>(null);
  warehouseNames = signal<Map<number, string>>(new Map());

  statuses = Object.keys(INBOUND_LABELS) as InboundStatus[];

  filterForm = this.fb.nonNullable.group({ supplier: '', status: '' });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (o, raw) => {
      const f = JSON.parse(raw) as { supplier: string; status: string };
      const okSupplier = !f.supplier ||
        (o.supplierName ?? '').toLowerCase().includes(f.supplier.toLowerCase());
      const okStatus = !f.status || o.status === f.status;
      return okSupplier && okStatus;
    };
    this.filterForm.valueChanges.subscribe((v) => {
      this.dataSource.filter = JSON.stringify(v);
      this.paginator?.firstPage();
    });
  }

  ngOnInit() {
    this.warehouseService.getAll().subscribe((list) =>
      this.warehouseNames.set(new Map((list ?? []).map((w) => [w.id, w.warehouseName]))));
    this.load();
  }
  ngAfterViewInit() { this.dataSource.paginator = this.paginator; }

  load() {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (list) => {
        this.dataSource.data = (list ?? []).sort((a, b) => b.orderId - a.orderId);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load inbound orders', 'OK', { duration: 4000 });
      },
    });
  }

  label(status: InboundStatus): string { return INBOUND_LABELS[status]; }
  nextStatuses(status: InboundStatus): InboundStatus[] { return INBOUND_TRANSITIONS[status]; }
  warehouseName(id: number) { return this.warehouseNames().get(id) ?? `#${id}`; }
  toggle(id: number) { this.expandedId.set(this.expandedId() === id ? null : id); }
  clearFilters() { this.filterForm.reset(); }

  openCreate() {
    this.dialog.open(InboundDialog, { width: '560px', maxWidth: '95vw' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open('Inbound order created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }

  changeStatus(order: InboundOrder, status: InboundStatus) {
    this.service.changeStatus(order.orderId, status).subscribe({
      next: () => {
        this.snack.open(`Order #${order.orderId}: ${INBOUND_LABELS[status]}`, 'OK', { duration: 3000 });
        this.load();
      },
      error: (err) =>
        this.snack.open(err.error?.message ?? 'Failed to change status', 'OK', { duration: 5000 }),
    });
  }
}
