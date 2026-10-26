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
import { Product, ProductService } from './product.service';
import { ProductDialog } from './product-dialog';

interface Filters { sku: string; name: string; category: string; }

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './products.html',
  styleUrls: ['./products.scss'],
})
export class Products implements OnInit, AfterViewInit {
  private service = inject(ProductService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['productSku', 'productName', 'unit', 'categoryName', 'actions'];
  dataSource = new MatTableDataSource<Product>([]);
  loading = signal(true);

  canEdit = computed(() => ['ADMIN', 'MANAGER'].includes(this.auth.session()?.role ?? ''));

  filterForm = this.fb.nonNullable.group({ sku: '', name: '', category: '' });

  constructor() {
    this.dataSource.filterPredicate = (p, raw) => {
      const f: Filters = JSON.parse(raw);
      const has = (value: string | null | undefined, needle: string) =>
        !needle || (value ?? '').toLowerCase().includes(needle.toLowerCase());
      return has(p.productSku, f.sku) && has(p.productName, f.name) && has(p.categoryName, f.category);
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
        this.snack.open('Failed to load products', 'OK', { duration: 4000 });
      },
    });
  }

  clearFilters() { this.filterForm.reset(); }

  openDialog(product?: Product) {
    this.dialog.open(ProductDialog, { data: product ?? null, width: '420px' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open(product ? 'Product updated' : 'Product created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }
}
