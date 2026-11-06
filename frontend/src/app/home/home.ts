import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { AuthService } from '../auth/auth.service';

interface Feature { icon: string; label: string; title: string; text: string; points: string[]; soon?: boolean; }
interface Step { icon: string; title: string; text: string; }
interface Role { icon: string; name: string; text: string; }

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, MatToolbarModule, MatButtonModule, MatIconModule, MatCardModule, MatTabsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class Home {
  auth = inject(AuthService);
  year = new Date().getFullYear();

  features: Feature[] = [
    {
      icon: 'inventory_2', label: 'Catalog', title: 'Products and categories',
      text: 'One clear catalog of everything you store.',
      points: ['Unique SKU for every product', 'Units of measure', 'Categories for fast search and filtering'],
    },
    {
      icon: 'local_shipping', label: 'Orders', title: 'Inbound and outbound orders',
      text: 'Full cycle of goods movement from supplier to customer.',
      points: ['Multi-item orders with quantities', 'Clear statuses: New, In transit, Shipped, Received, Cancelled', 'Only valid status transitions are allowed'],
    },
    {
      icon: 'event_note', label: 'Batches', title: 'Batches and expiry dates',
      text: 'Track lot numbers and never miss an expiring batch.',
      points: ['Lot number unique per product', 'Expiry highlighting: expiring soon and expired', 'Optional supplier for every batch'],
    },
    {
      icon: 'warehouse', label: 'Warehouses', title: 'Warehouses and locations',
      text: 'Describe exactly where things are stored.',
      points: ['Any number of warehouses', 'Zone, aisle, rack, shelf and bin for every location', 'Capacity and unique codes per warehouse'],
    },
    {
      icon: 'admin_panel_settings', label: 'Access', title: 'Roles and security',
      text: 'Everyone sees what they need and changes only what they may.',
      points: ['Three roles: Worker, Manager, Admin', 'Secure sign-in with tokens', 'Permissions enforced on the server'],
    },
    {
      icon: 'data_check', label: 'Stock', title: 'Stock levels and movements',
      text: 'Automatic stock updates when orders are received or shipped.',
      points: ['Stock per warehouse and batch', 'Full history of every movement', 'Low stock alerts'],

    },
  ];

  steps: Step[] = [
    { icon: 'tune', title: '1. Set up', text: 'Add warehouses, locations, products, suppliers and customers.' },
    { icon: 'receipt_long', title: '2. Process orders', text: 'Create inbound and outbound orders and move them through statuses.' },
    { icon: 'visibility', title: '3. Stay in control', text: 'Watch batches and expiry dates, and see who did what.' },
  ];

  roles: Role[] = [
    { icon: 'engineering', name: 'Worker', text: 'Browses the catalog, orders and batches. Read-only access.' },
    { icon: 'manage_accounts', name: 'Manager', text: 'Creates and edits records and moves orders through statuses.' },
    { icon: 'admin_panel_settings', name: 'Admin', text: 'Full access to everything in the system.' },
  ];
}
