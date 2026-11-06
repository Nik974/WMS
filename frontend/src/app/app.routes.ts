import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import {roleGuard} from './auth/auth.guard';
import {Dashboard} from './dashboard/dashboard';
import {Products} from './products/products';
import {Categories} from './categories/categories';
import {Warehouses} from './warehouses/warehouses';
import { Customers } from './customers/customers';
import {Orders} from './orders/orders';
import {Suppliers} from './suppliers/suppliers';
import {InboundOrders} from './inbound-orders/inbound-orders';
import {Locations} from './locations/locations';
import {Batches} from './batches/batches';
import {Home} from './home/home';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: Dashboard, canActivate: [roleGuard] },
  { path: 'products', component: Products, canActivate: [roleGuard] },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: Home },
  { path: 'categories', component: Categories, canActivate: [roleGuard] },
  { path: 'warehouses', component: Warehouses, canActivate: [roleGuard] },
  { path: 'customers', component: Customers, canActivate: [roleGuard] },
  { path: 'outbound-orders', component: Orders, canActivate: [roleGuard] },
  { path: 'suppliers', component: Suppliers, canActivate: [roleGuard] },
  { path: 'inbound-orders', component: InboundOrders, canActivate: [roleGuard] },
  { path: 'locations', component: Locations, canActivate: [roleGuard] },
  { path: 'batches', component: Batches, canActivate: [roleGuard] },
  { path: '**', redirectTo: '' }
];
