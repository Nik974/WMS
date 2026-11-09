import {Component, computed, inject} from '@angular/core';
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
  roles?: string[];
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

  private allTiles: Tile[] = [
    {label: 'Products', icon: 'inventory_2', link: '/products', enabled: true},
    {label: 'Categories', icon: 'category', link: '/categories', enabled: true},
    {label: 'Batches', icon: 'event_note', link: '/batches', enabled: true},
    {label: 'Warehouses', icon: 'warehouse', link: '/warehouses', enabled: true},
    {label: 'Locations', icon: 'pin_drop', link: '/locations', enabled: true},
    {label: 'Customers', icon: 'groups', link: '/customers', enabled: true},
    {label: 'Suppliers', icon: 'store', link: '/suppliers', enabled: true},
    {label: 'Outbound orders', icon: 'output', link: '/outbound-orders', enabled: true},
    {label: 'Inbound orders', icon: 'system_update_alt', link: '/inbound-orders', enabled: true},
    { label: 'Users', icon: 'manage_accounts', link: '/users', enabled: true, roles: ['ADMIN'] },
  ];

  tiles = computed(() => {
    const role = (this.auth.session()?.role ?? '').toUpperCase().replace('ROLE_', '');
    return this.allTiles.filter((t) => !t.roles || t.roles.includes(role));
  });

}
