import {Component, inject, OnInit, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {debounceTime} from 'rxjs';
import {Header} from '../shared/header/header';
import {AuditEntry, AuditService} from './audit.service';

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [
    DatePipe, ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, Header,
  ],
  templateUrl: './audit-log.html',
  styleUrls: ['./audit-log.scss'],
})
export class AuditLog implements OnInit {
  private service = inject(AuditService);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  columns = ['time', 'user', 'description'];
  rows = signal<AuditEntry[]>([]);
  total = signal(0);
  pageIndex = signal(0);
  pageSize = signal(25);
  loading = signal(true);

  resources = [
    'categories', 'products', 'batches', 'warehouses', 'locations', 'customers',
    'suppliers', 'outbound-orders', 'inbound-orders', 'stocks', 'users',
  ];

  filterForm = this.fb.nonNullable.group({user: '', resource: '', from: '', to: ''});

  constructor() {
    this.filterForm.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.pageIndex.set(0);
      this.load();
    });
  }

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.service.search(this.filterForm.getRawValue(), this.pageIndex(), this.pageSize()).subscribe({
      next: (p) => {
        this.rows.set(p.content ?? []);
        this.total.set(p.totalElements ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snack.open('Failed to load audit log', 'OK', {duration: 4000});
      },
    });
  }

  onPage(e: PageEvent) {
    this.pageIndex.set(e.pageIndex);
    this.pageSize.set(e.pageSize);
    this.load();
  }

  clearFilters() {
    this.filterForm.reset();
  }

  text(e: AuditEntry): string {
    if (e.description) return e.description;
    const target = e.resourceId ? `${e.resource} #${e.resourceId}` : e.resource;
    return `${e.httpMethod} ${target}${e.action ? ' ' + e.action : ''}`;
  }
}
