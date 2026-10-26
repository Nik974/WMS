import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import {roleGuard} from './auth/auth.guard';
import {Dashboard} from './dashboard/dashboard';
import {Products} from './products/products';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: Dashboard, canActivate: [roleGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'products', component: Products, canActivate: [roleGuard] },
  { path: '', redirectTo: 'products', pathMatch: 'full' },
];
