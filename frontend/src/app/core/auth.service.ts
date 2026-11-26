import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';
import { AuthResponse, Role, Spot, UserSummary } from './models';

const STORAGE_KEY = 'exorcity.session';

interface Session {
  token: string;
  expiresAt: string;
  user: UserSummary;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly session = signal<Session | null>(this.restore());

  readonly user = computed(() => this.session()?.user ?? null);
  readonly token = computed(() => this.session()?.token ?? null);
  readonly isLoggedIn = computed(() => this.session() !== null);
  readonly isCitizen = computed(() => this.user()?.role === 'CITIZEN');
  readonly isEntityAdmin = computed(() => {
    const r = this.user()?.role;
    return r === 'SOCIETY_ADMIN' || r === 'CORPORATE_ADMIN' || r === 'SHOP_OWNER';
  });
  readonly isWardOfficer = computed(() => this.user()?.role === 'WARD_OFFICER');

  login(email: string, password: string): Observable<AuthResponse> {
    return this.api.login(email, password).pipe(tap((r) => this.store(r)));
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.api.register(name, email, password).pipe(tap((r) => this.store(r)));
  }

  logout(redirect = true): void {
    this.session.set(null);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      /* ignore */
    }
    if (redirect) this.router.navigateByUrl('/login');
  }

  hasRole(...roles: Role[]): boolean {
    const r = this.user()?.role;
    return !!r && roles.includes(r);
  }

  /** Keeps the cached score in the header in sync after actions. */
  updateScore(score: number): void {
    const s = this.session();
    if (!s) return;
    this.persist({ ...s, user: { ...s.user, civicScore: score } });
  }

  canClean(spot: Spot): boolean {
    const u = this.user();
    if (!u || spot.status !== 'OPEN') return false;
    if (u.role === 'CITIZEN') return true;
    if (this.isEntityAdmin()) return u.entityId !== null && u.entityId === spot.linkedEntityId;
    if (u.role === 'WARD_OFFICER') return !!u.ward && u.ward.toLowerCase() === (spot.ward ?? '').toLowerCase();
    return false;
  }

  homeFor(role: Role | undefined): string {
    if (role === 'WARD_OFFICER' || role === 'SOCIETY_ADMIN' || role === 'CORPORATE_ADMIN' || role === 'SHOP_OWNER') {
      return '/dashboard';
    }
    return '/map';
  }

  private store(r: AuthResponse): void {
    this.persist({ token: r.token, expiresAt: r.expiresAt, user: r.user });
  }

  private persist(s: Session): void {
    this.session.set(s);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(s));
    } catch {
      /* storage blocked: session lasts for this tab only */
    }
  }

  private restore(): Session | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) return null;
      const s = JSON.parse(raw) as Session;
      return new Date(s.expiresAt).getTime() > Date.now() ? s : null;
    } catch {
      return null;
    }
  }
}
