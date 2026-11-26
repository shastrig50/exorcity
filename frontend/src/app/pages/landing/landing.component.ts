import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { LEVELS, MONSTERS } from '../../core/monsters';
import { SpotStats } from '../../core/models';
import { MonsterAvatarComponent } from '../../shared/monster-avatar.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink, MonsterAvatarComponent],
  template: `
    <section class="relative -mx-4 -mt-6 overflow-hidden px-4 pb-16 pt-20 text-center sm:pt-28" aria-labelledby="hero-title">
      <div class="fog-layer" aria-hidden="true"></div>
      <div class="fog-layer fog-2" aria-hidden="true"></div>
      <div class="relative">
        <p class="mb-4 text-sm font-semibold uppercase tracking-[0.3em] text-mist">Bengaluru · Kiroween 2026</p>
        <h1 id="hero-title" class="text-6xl text-pumpkin drop-shadow-[0_0_18px_rgba(255,107,0,0.45)] sm:text-8xl">Exorcity</h1>
        <p class="mx-auto mt-5 max-w-2xl text-xl text-bone sm:text-2xl">
          Your city is haunted by garbage. <span class="text-ecto">Exorcise it</span>, and get rewarded.
        </p>

        <div class="mx-auto mt-10 inline-flex flex-col items-center gap-1 rounded-2xl border border-blood/60 bg-crypt/80 px-8 py-5 shadow-[0_0_40px_rgba(139,0,0,0.35)]"
             aria-live="polite">
          @if (stats(); as s) {
            <span class="text-5xl font-bold tabular-nums text-bone">{{ s.openMonsters }}</span>
            <span class="text-mist">monsters haunting the city right now</span>
            <span class="mt-2 text-sm text-mist">
              <span class="font-semibold text-[#ff7a7a]">{{ s.cursedMonsters }} cursed</span> ·
              <span class="font-semibold text-ecto">{{ s.exorcised }} exorcised</span>
            </span>
          } @else if (failed()) {
            <span class="text-mist">The spirits are quiet… (backend unreachable)</span>
          } @else {
            <span class="text-mist">Counting monsters…</span>
          }
        </div>

        <div class="mt-10 flex flex-wrap justify-center gap-3">
          <a routerLink="/map" class="btn-primary px-8 text-lg no-underline hover:no-underline">Start Hunting</a>
          @if (!auth.isLoggedIn()) {
            <a routerLink="/login" class="btn-ghost no-underline hover:no-underline">🔑 Try a demo login</a>
          }
          <a routerLink="/leaderboards" class="btn-ghost no-underline hover:no-underline">See the leaderboards</a>
        </div>
        @if (!auth.isLoggedIn()) {
          <p class="mt-4 text-sm text-mist">One-click demo accounts for every role · password <code class="text-bone">Demo&#64;123</code></p>
        }
      </div>
    </section>

    <section class="mt-6" aria-labelledby="how-title">
      <h2 id="how-title" class="mb-6 text-center text-2xl font-bold text-bone">How the exorcism works</h2>
      <ol class="grid gap-4 md:grid-cols-3">
        @for (step of steps; track step.title; let i = $index) {
          <li class="card">
            <div class="mb-2 text-3xl" aria-hidden="true">{{ step.icon }}</div>
            <h3 class="font-semibold text-pumpkin">{{ i + 1 }}. {{ step.title }}</h3>
            <p class="mt-1 text-mist">{{ step.text }}</p>
          </li>
        }
      </ol>
    </section>

    <section class="mt-12" aria-labelledby="bestiary-title">
      <h2 id="bestiary-title" class="mb-2 text-center text-2xl font-bold text-bone">The bestiary</h2>
      <p class="mb-6 text-center text-mist">Every kind of garbage has its own monster. The older it gets, the bigger and scarier it grows.</p>
      <ul class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
        @for (m of monsters; track m.type) {
          <li class="card flex flex-col items-center gap-3 text-center">
            <app-monster-avatar [emoji]="m.emoji" [level]="3" />
            <div>
              <div class="font-semibold text-bone">{{ m.name }}</div>
              <div class="text-sm text-mist">{{ m.label }}</div>
            </div>
          </li>
        }
      </ul>
      <div class="card mt-6">
        <h3 class="mb-4 font-semibold text-bone">Scarier monster = more urgent problem</h3>
        <ul class="flex flex-wrap items-end justify-around gap-4">
          @for (l of levels; track l.level) {
            <li class="flex flex-col items-center gap-2">
              <app-monster-avatar emoji="🧟" [level]="l.level" />
              <span class="text-sm font-semibold text-bone">Level {{ l.level }}</span>
              <span class="text-xs text-mist">{{ l.title }} · {{ l.range }}</span>
            </li>
          }
          <li class="flex flex-col items-center gap-2">
            <app-monster-avatar emoji="🕷️" [level]="3" [cursed]="true" />
            <span class="text-sm font-semibold text-[#ff7a7a]">Cursed</span>
            <span class="text-xs text-mist">3+ spots within 50 m</span>
          </li>
        </ul>
      </div>
    </section>
  `,
})
export class LandingComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly auth = inject(AuthService);
  readonly stats = signal<SpotStats | null>(null);
  readonly failed = signal(false);
  readonly monsters = MONSTERS;
  readonly levels = LEVELS;
  readonly steps = [
    { icon: '📸', title: 'Spot it', text: 'Report garbage with a photo and location. It becomes a monster on the haunted map.' },
    { icon: '👀', title: 'Confirm it', text: 'Fellow hunters confirm sightings. Every 5 confirmations makes the monster scarier.' },
    { icon: '✨', title: 'Exorcise it', text: 'Clean it with an after photo. The monster vanishes in smoke and you earn Civic Score.' },
  ];

  ngOnInit(): void {
    this.api.stats().subscribe({ next: (s) => this.stats.set(s), error: () => this.failed.set(true) });
  }
}
