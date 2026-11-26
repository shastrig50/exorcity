import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { MotionService } from './core/motion.service';
import { ToastOutletComponent } from './shared/toast-outlet.component';

interface NavLink {
  path: string;
  label: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastOutletComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  readonly motion = inject(MotionService);
  readonly auth = inject(AuthService);
  readonly menuOpen = signal(false);

  readonly links = computed<NavLink[]>(() => {
    const links: NavLink[] = [{ path: '/map', label: 'Haunted Map' }];
    if (this.auth.isCitizen()) links.push({ path: '/report', label: 'Report' });
    if (this.auth.isEntityAdmin() || this.auth.isWardOfficer()) links.push({ path: '/dashboard', label: 'Dashboard' });
    links.push({ path: '/leaderboards', label: 'Leaderboards' });
    if (this.auth.isLoggedIn()) links.push({ path: '/profile', label: 'My Profile' });
    return links;
  });

  toggleMenu(): void {
    this.menuOpen.update((v) => !v);
  }

  logout(): void {
    this.menuOpen.set(false);
    this.auth.logout();
  }
}
