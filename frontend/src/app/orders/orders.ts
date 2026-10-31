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
  ORDER_TRANSITIONS, Order, OrderService, OrderStatus, STATUS_LABELS,
} from './order.service';
import { OrderDialog } from './order-dialog';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
    MatMenuModule, Header,
  ],
  templateUrl: './orders.html',
  styleUrls: ['./orders.scss'],
})
export class Orders implements OnInit, AfterViewInit {
  private service = inject(OrderService);
  private warehouseService = inject(WarehouseService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['id', 'customerName', 'warehouse', 'status', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<Order>([]);
  loading = signal(true);
  expandedId = signal<number | null>(null);
  warehouseNames = signal<Map<number, string>>(new Map());

  statuses = Object.keys(STATUS_LABELS) as OrderStatus[];
  labels = STATUS_LABELS;
  transitions = ORDER_TRANSITIONS;
  label(status: OrderStatus): string {
    return STATUS_LABELS[status];
  }

  nextStatuses(status: OrderStatus): OrderStatus[] {
    return ORDER_TRANSITIONS[status];
  }
  filterForm = this.fb.nonNullable.group({ customer: '', status: '' });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (o, raw) => {
      const f = JSON.parse(raw) as { customer: string; status: string };
      const okCustomer = !f.customer || (o.customerName ?? '').toLowerCase().includes(f.customer.toLowerCase());
      const okStatus = !f.status || o.status === f.status;
      return okCustomer && okStatus;
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
        this.dataSource.data = (list ?? []).sort((a, b) => b.id - a.id);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load orders', 'OK', { duration: 4000 });
      },
    });
  }

  warehouseName(id: number) { return this.warehouseNames().get(id) ?? `#${id}`; }
  toggle(id: number) { this.expandedId.set(this.expandedId() === id ? null : id); }
  clearFilters() { this.filterForm.reset(); }

  openCreate() {
    this.dialog.open(OrderDialog, { width: '560px', maxWidth: '95vw' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open('Order created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }

  changeStatus(order: Order, status: OrderStatus) {
    this.service.changeStatus(order.id, status).subscribe({
      next: () => {
        this.snack.open(`Order #${order.id}: ${STATUS_LABELS[status]}`, 'OK', { duration: 3000 });
        this.load();
      },
      error: (err) =>
        this.snack.open(err.error?.message ?? 'Failed to change status', 'OK', { duration: 5000 }),
    });
  }
}
