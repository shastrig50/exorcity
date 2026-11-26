import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards';

export const routes: Routes = [
  { path: '', title: 'Exorcity', loadComponent: () => import('./pages/landing/landing.component').then((m) => m.LandingComponent) },
  { path: 'map', title: 'Haunted Map · Exorcity', loadComponent: () => import('./pages/map/map.component').then((m) => m.MapComponent) },
  { path: 'login', title: 'Log in · Exorcity', loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent) },
  {
    path: 'report', title: 'Report garbage · Exorcity', canActivate: [roleGuard('CITIZEN')],
    loadComponent: () => import('./pages/report/report.component').then((m) => m.ReportComponent),
  },
  { path: 'spots/:id', title: 'Spot details · Exorcity', loadComponent: () => import('./pages/spot-detail/spot-detail.component').then((m) => m.SpotDetailComponent) },
  { path: 'profile', title: 'My profile · Exorcity', canActivate: [authGuard], loadComponent: () => import('./pages/profile/profile.component').then((m) => m.ProfileComponent) },
  {
    path: 'dashboard', title: 'Dashboard · Exorcity',
    canActivate: [roleGuard('SOCIETY_ADMIN', 'CORPORATE_ADMIN', 'SHOP_OWNER', 'WARD_OFFICER')],
    loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  { path: 'leaderboards', title: 'Leaderboards · Exorcity', loadComponent: () => import('./pages/leaderboards/leaderboards.component').then((m) => m.LeaderboardsComponent) },
  { path: '**', redirectTo: '' },
];
