import {AfterViewInit, Component, inject, OnInit, signal, ViewChild} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Header} from '../shared/header/header';
import {Movement, MovementService} from './movement.service';

@Component({
  selector: 'app-movements',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, Header,
  ],
  templateUrl: './movements.html',
  styleUrls: ['./movements.scss'],
})
export class Movements implements OnInit, AfterViewInit {
  private service = inject(MovementService);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['time', 'type', 'product', 'route', 'quantity', 'reference', 'user'];
  dataSource = new MatTableDataSource<Movement>([]);
  loading = signal(true);

  types = ['RECEIPT', 'SHIPMENT', 'ADJUSTMENT', 'TRANSFER'];
  typeLabels: Record<string, string> = {
    RECEIPT: 'Receipt', SHIPMENT: 'Shipment', ADJUSTMENT: 'Adjustment', TRANSFER: 'Transfer',
  };

  filterForm = this.fb.nonNullable.group({product: '', user: '', type: ''});

  constructor() {
    this.dataSource.filterPredicate = (m, raw) => {
      const f = JSON.parse(raw) as { product: string; user: string; type: string };
      const has = (v: string | null | undefined, n: string) =>
        !n || (v ?? '').toLowerCase().includes(n.toLowerCase());
      return (has(m.productName, f.product) || has(m.lotNumber, f.product))
        && has(m.username, f.user)
        && (!f.type || m.movementType === f.type);
    };
    this.filterForm.valueChanges.subscribe((v) => {
      this.dataSource.filter = JSON.stringify(v);
      this.paginator?.firstPage();
    });
  }

  ngOnInit() {
    this.load();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  load() {
    this.loading.set(true);
    this.service.getRecent().subscribe({
      next: (list) => {
        this.dataSource.data = list ?? [];
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load movements', 'OK', {duration: 4000});
      },
    });
  }

  label(type: string): string {
    return this.typeLabels[type] ?? type;
  }

  route(m: Movement): string {
    const from = m.fromLocationCode ?? (m.fromLocationId ? `#${m.fromLocationId}` : null);
    const to = m.toLocationCode ?? (m.toLocationId ? `#${m.toLocationId}` : null);
    if (from && to) return `${from} → ${to}`;
    if (to) return `→ ${to}`;
    if (from) return `${from} →`;
    return '—';
  }

  sign(m: Movement): string {
    if (m.fromLocationId && m.toLocationId) return '';
    return m.toLocationId ? '+' : '−';
  }

  direction(m: Movement): string {
    if (m.fromLocationId && m.toLocationId) return 'move';
    return m.toLocationId ? 'in' : 'out';
  }

  reference(m: Movement): string {
    switch (m.referenceType) {
      case 'INBOUND':
        return `Inbound #${m.referenceId}`;
      case 'OUTBOUND':
        return `Outbound #${m.referenceId}`;
      case 'MANUAL':
        return m.comment ? `Manual: ${m.comment}` : 'Manual';
      default:
        return m.comment ?? '—';
    }
  }

  clearFilters() {
    this.filterForm.reset();
  }
}
