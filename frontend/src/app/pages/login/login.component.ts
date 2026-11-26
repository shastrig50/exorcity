import { Component, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ROLE_LABELS } from '../../core/monsters';
import { Role } from '../../core/models';
import { ToastService, errorMessage } from '../../core/toast.service';

interface DemoUser {
  role: Role;
  email: string;
  name: string;
  hint: string;
  icon: string;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="mx-auto grid max-w-5xl gap-8 lg:grid-cols-2">
      <section class="card" aria-labelledby="login-title">
        <h1 id="login-title" class="text-4xl text-pumpkin">{{ mode() === 'login' ? 'Enter the crypt' : 'Join the hunt' }}</h1>
        <p class="mt-1 text-mist">{{ mode() === 'login' ? 'Log in to report, confirm and exorcise.' : 'Create a ghost hunter account.' }}</p>

        <form class="mt-6 space-y-4" [formGroup]="form" (ngSubmit)="submit()" novalidate>
          @if (mode() === 'register') {
            <div>
              <label for="name" class="block text-sm font-semibold">Name</label>
              <input id="name" formControlName="name" autocomplete="name"
                     class="mt-1 w-full rounded-lg border border-white/10 bg-night px-3 py-2" />
            </div>
          }
          <div>
            <label for="email" class="block text-sm font-semibold">Email</label>
            <input id="email" type="email" formControlName="email" autocomplete="email"
                   class="mt-1 w-full rounded-lg border border-white/10 bg-night px-3 py-2" />
          </div>
          <div>
            <label for="password" class="block text-sm font-semibold">Password</label>
            <input id="password" type="password" formControlName="password"
                   [attr.autocomplete]="mode() === 'login' ? 'current-password' : 'new-password'"
                   aria-describedby="password-hint"
                   class="mt-1 w-full rounded-lg border border-white/10 bg-night px-3 py-2" />
            @if (mode() === 'register') {
              <p id="password-hint" class="mt-1 text-xs text-mist">At least 8 characters.</p>
            }
          </div>
          @if (error()) { <p class="text-sm text-[#ff8a8a]" role="alert">{{ error() }}</p> }
          <button type="submit" class="btn-primary w-full" [disabled]="busy()">
            {{ busy() ? 'Opening the gates…' : mode() === 'login' ? 'Log in' : 'Create account' }}
          </button>
        </form>

        <p class="mt-4 text-sm text-mist">
          {{ mode() === 'login' ? 'New here?' : 'Already a hunter?' }}
          <button type="button" class="font-semibold text-pumpkin hover:underline" (click)="toggleMode()">
            {{ mode() === 'login' ? 'Create an account' : 'Log in instead' }}
          </button>
        </p>
      </section>

      <section class="card" aria-labelledby="demo-title">
        <h2 id="demo-title" class="text-xl font-bold text-bone">Quick demo login</h2>
        <p class="mt-1 text-sm text-mist">One click per role. All demo users share the password <code class="text-bone">Demo&#64;123</code>.</p>
        <ul class="mt-5 grid gap-3">
          @for (d of demoUsers; track d.email) {
            <li>
              <button type="button" class="flex w-full items-center gap-4 rounded-lg border border-white/10 bg-night px-4 py-3 text-left transition hover:border-pumpkin disabled:opacity-50"
                      (click)="demo(d)" [disabled]="busy()" [attr.aria-label]="'Log in as ' + d.name + ', ' + roleLabels[d.role]">
                <span class="text-2xl" aria-hidden="true">{{ d.icon }}</span>
                <span class="flex-1">
                  <span class="block font-semibold text-bone">{{ roleLabels[d.role] }} · {{ d.name }}</span>
                  <span class="block text-xs text-mist">{{ d.hint }}</span>
                </span>
              </button>
            </li>
          }
        </ul>
      </section>
    </div>
  `,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  /** Bound from the ?next= query param. */
  readonly next = input<string | undefined>();
  readonly mode = signal<'login' | 'register'>('login');
  readonly busy = signal(false);
  readonly error = signal('');
  readonly roleLabels = ROLE_LABELS;

  readonly form = this.fb.nonNullable.group({
    name: [''],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  readonly demoUsers: DemoUser[] = [
    { role: 'CITIZEN', email: 'citizen@exorcity.demo', name: 'Asha Rao', icon: '🔦', hint: 'Report, confirm and clean spots' },
    { role: 'SOCIETY_ADMIN', email: 'society@exorcity.demo', name: 'Sunita Menon', icon: '🏘️', hint: 'Green Meadows Society (cursed cluster!)' },
    { role: 'CORPORATE_ADMIN', email: 'corporate@exorcity.demo', name: 'Vikram Shah', icon: '🏢', hint: 'Tech Park Alpha, Whitefield' },
    { role: 'SHOP_OWNER', email: 'shop@exorcity.demo', name: 'Mohan Gowda', icon: '🛒', hint: 'Corner Kirana Store, Malleshwaram' },
    { role: 'WARD_OFFICER', email: 'ward@exorcity.demo', name: 'Rekha Iyer', icon: '🛡️', hint: 'Koramangala ward: clean or reject reports' },
  ];

  toggleMode(): void {
    this.mode.update((m) => (m === 'login' ? 'register' : 'login'));
    this.error.set('');
  }

  demo(d: DemoUser): void {
    this.form.patchValue({ email: d.email, password: 'Demo@123' });
    this.mode.set('login');
    this.submit();
  }

  submit(): void {
    const { name, email, password } = this.form.getRawValue();
    if (this.form.invalid || (this.mode() === 'register' && (!name.trim() || password.length < 8))) {
      this.error.set(this.mode() === 'register' ? 'Enter your name, a valid email and a password of 8+ characters.'
        : 'Enter a valid email and password.');
      return;
    }
    this.busy.set(true);
    this.error.set('');
    const call = this.mode() === 'login' ? this.auth.login(email, password) : this.auth.register(name, email, password);
    call.subscribe({
      next: (r) => {
        this.busy.set(false);
        this.toast.success(`Welcome, ${r.user.name}. The hunt begins.`);
        this.router.navigateByUrl(this.next() || this.auth.homeFor(r.user.role));
      },
      error: (e) => {
        this.busy.set(false);
        this.error.set(errorMessage(e, 'Login failed'));
      },
    });
  }
}
