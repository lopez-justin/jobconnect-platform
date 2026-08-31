import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login').then((m) => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register').then((m) => m.RegisterComponent),
      },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },
  {
    path: '',
    loadComponent: () => import('./features/layout/layout').then((m) => m.LayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'jobs', pathMatch: 'full' },
      {
        path: 'jobs',
        loadComponent: () =>
          import('./features/jobs/job-list/job-list').then((m) => m.JobListComponent),
      },
      {
        path: 'jobs/create',
        loadComponent: () =>
          import('./features/jobs/job-create/job-create').then((m) => m.JobCreateComponent),
      },
      {
        path: 'transactions',
        loadComponent: () =>
          import('./features/transactions/transactions-list/transactions-list').then(
            (m) => m.TransactionsListComponent,
          ),
      },
    ],
  },
  { path: '**', redirectTo: 'auth/login' },
];
