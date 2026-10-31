import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Header } from '../shared/header/header';
import { AuthService } from '../auth/auth.service';

interface Tile {
  label: string;
  icon: string;
  link: string;
  enabled: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule, Header],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class Dashboard {
  auth = inject(AuthService);

  tiles: Tile[] = [
    { label: 'Products', icon: 'inventory_2', link: '/products', enabled: true },
    { label: 'Categories', icon: 'category', link: '/categories', enabled: true },
    { label: 'Warehouses', icon: 'warehouse', link: '/warehouses', enabled: true },
    { label: 'Customers', icon: 'groups', link: '/customers', enabled: true },
    { label: 'Outbound orders', icon: 'local_shipping', link: '/orders', enabled: true },
  ];
}
