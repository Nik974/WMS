import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import {roleGuard} from './auth/auth.guard';
import {Dashboard} from './dashboard/dashboard';
import {Products} from './products/products';
import {Categories} from './categories/categories';
import {Warehouses} from './warehouses/warehouses';
import { Customers } from './customers/customers';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: Dashboard, canActivate: [roleGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'products', component: Products, canActivate: [roleGuard] },
  { path: '', redirectTo: 'products', pathMatch: 'full' },
  { path: 'categories', component: Categories, canActivate: [roleGuard] },
  { path: 'warehouses', component: Warehouses, canActivate: [roleGuard] },
  { path: 'customers', component: Customers, canActivate: [roleGuard] },
];
