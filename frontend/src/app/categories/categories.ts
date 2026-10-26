import { AfterViewInit, Component, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
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
import { Category, CategoryService } from './category.service';
import { CategoryDialog } from './category-dialog';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatTableModule, MatPaginatorModule, MatExpansionModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, Header,
  ],
  templateUrl: './categories.html',
  styleUrls: ['./categories.scss'],
})
export class Categories implements OnInit, AfterViewInit {
  private service = inject(CategoryService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);
  private auth = inject(AuthService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  columns = ['categoryName', 'actions'];
  dataSource = new MatTableDataSource<Category>([]);
  loading = signal(true);
  search = new FormControl('', { nonNullable: true });

  canEdit = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return role === 'ADMIN' || role === 'MANAGER';
  });

  constructor() {
    this.dataSource.filterPredicate = (c, needle) =>
      !needle || c.categoryName.toLowerCase().includes(needle.toLowerCase());

    this.search.valueChanges.subscribe((v) => {
      this.dataSource.filter = v.trim();
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
        this.snack.open('Failed to load categories', 'OK', { duration: 4000 });
      },
    });
  }

  openDialog(category?: Category) {
    this.dialog.open(CategoryDialog, { data: category ?? null, width: '420px' })
      .afterClosed().subscribe((saved) => {
      if (saved) {
        this.snack.open(category ? 'Category updated' : 'Category created', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }
}
