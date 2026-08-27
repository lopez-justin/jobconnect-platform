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
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard').then((m) => m.DashboardComponent),
    canActivate: [authGuard],
  },

  /*{
    path: 'jobs',
    loadChildren: () => import('./features/jobs/jobs.routes').then((m) => m.JOB_ROUTES),
    canActivate: [authGuard],
  },*/

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  { path: '**', redirectTo: 'auth/login' },
];
