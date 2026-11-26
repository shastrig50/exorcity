import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ENTITY_TYPE_LABELS, levelInfo } from '../../core/monsters';
import { Dashboard, Spot, WardScore } from '../../core/models';
import { ToastService, errorMessage } from '../../core/toast.service';
import { CandleLoaderComponent } from '../../shared/candle-loader.component';
import { MonsterAvatarComponent } from '../../shared/monster-avatar.component';
import { RatingBadgeComponent } from '../../shared/rating-badge.component';
import { ScoreMeterComponent } from '../../shared/score-meter.component';

/**
 * Entity admins: their entity's linked spots, score and history.
 * Ward officers: every open spot in their ward, with clean/reject actions on each spot page.
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DatePipe, RouterLink, CandleLoaderComponent, MonsterAvatarComponent, RatingBadgeComponent, ScoreMeterComponent],
  template: `
    @if (loading()) {
      <app-candle-loader />
    } @else if (error()) {
      <div class="card" role="alert">{{ error() }}</div>
    } @else {
    @if (dashboard(); as d) {
      <div class="grid gap-6 lg:grid-cols-[340px_1fr]">
        <section class="card text-center" aria-labelledby="dash-title">
          <p class="text-xs uppercase tracking-widest text-mist">{{ typeLabels[d.entity.type] }} · {{ d.entity.ward }}</p>
          <h1 id="dash-title" class="mt-1 text-3xl text-pumpkin">{{ d.entity.name }}</h1>
          <div class="mt-4"><app-score-meter [score]="d.entity.civicScore" /></div>
          <div class="mt-3"><app-rating-badge [rating]="d.entity.rating" /></div>
          <dl class="mt-6 grid grid-cols-2 gap-3 text-center">
            <div class="rounded-lg bg-night p-3"><dt class="text-xs text-mist">Open</dt><dd class="text-2xl font-bold text-bone">{{ d.stats.openSpots }}</dd></div>
            <div class="rounded-lg bg-night p-3"><dt class="text-xs text-mist">Exorcised</dt><dd class="text-2xl font-bold text-ecto">{{ d.stats.cleanedSpots }}</dd></div>
            <div class="rounded-lg bg-night p-3"><dt class="text-xs text-mist">Overdue (7d+)</dt><dd class="text-2xl font-bold text-[#ff8a5c]">{{ d.stats.overdueSpots }}</dd></div>
            <div class="rounded-lg bg-night p-3"><dt class="text-xs text-mist">Cursed</dt><dd class="text-2xl font-bold text-[#ff7a7a]">{{ d.stats.cursedSpots }}</dd></div>
          </dl>
          <p class="mt-4 text-xs text-mist">+20 for every linked spot cleaned within 3 days · −15 once a spot is 7+ days old · −50 when a spot becomes cursed.</p>
        </section>

        <div class="space-y-6">
          <section class="card" aria-labelledby="spots-title">
            <h2 id="spots-title" class="mb-4 text-xl font-bold text-bone">Linked spots (within 200 m)</h2>
            <ul class="divide-y divide-white/5">
              @for (s of d.spots; track s.id) {
                <li class="flex items-center gap-4 py-3">
                  @if (s.status === 'OPEN') {
                    <app-monster-avatar [emoji]="s.monsterEmoji" [level]="s.monsterLevel" [cursed]="s.cursed" [scale]="0.8" />
                  } @else {
                    <span class="flex w-[42px] justify-center text-2xl" aria-hidden="true">✨</span>
                  }
                  <div class="min-w-0 flex-1">
                    <p class="font-semibold text-bone">{{ s.monsterName }}
                      <span class="text-sm font-normal text-mist">· {{ s.status === 'OPEN' ? 'Level ' + s.monsterLevel + ', ' + s.ageDays + ' days' : 'exorcised ' + (s.cleanedAt | date: 'mediumDate') }}</span>
                    </p>
                    <p class="truncate text-sm text-mist">{{ s.description }}</p>
                  </div>
                  @if (s.status === 'OPEN') {
                    <a [routerLink]="['/spots', s.id]" class="btn-primary shrink-0 text-sm no-underline hover:no-underline"
                       [attr.aria-label]="'Clean ' + s.monsterName + ': ' + (s.description || '')">Clean</a>
                  }
                </li>
              } @empty {
                <li class="py-6 text-center text-mist">No spots linked to you. Ghost free!</li>
              }
            </ul>
          </section>

          <section class="card" aria-labelledby="ehist-title">
            <h2 id="ehist-title" class="mb-3 text-xl font-bold text-bone">Score history</h2>
            <ul class="divide-y divide-white/5 text-sm">
              @for (e of d.history; track e.id) {
                <li class="flex gap-4 py-2">
                  <span class="w-12 text-right font-bold tabular-nums" [class]="e.points >= 0 ? 'text-ecto' : 'text-[#ff7a7a]'">{{ e.points > 0 ? '+' : '' }}{{ e.points }}</span>
                  <span class="flex-1 text-bone">{{ e.description }}</span>
                  <span class="text-mist">{{ e.createdAt | date: 'mediumDate' }}</span>
                </li>
              } @empty {
                <li class="py-3 text-mist">No score changes yet.</li>
              }
            </ul>
          </section>
        </div>
      </div>
    } @else if (auth.isWardOfficer()) {
      <div class="mb-4 flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 class="text-4xl text-pumpkin">{{ ward() }} ward</h1>
          <p class="text-mist">Every open spot in your ward, scariest first. Open a spot to clean it or reject a fake report.</p>
        </div>
        <button type="button" class="btn-ghost" (click)="runJob()" [disabled]="jobBusy()">⏱ Run daily overdue job now</button>
      </div>
      @if (wardScore(); as w) {
        <div class="mb-4 grid grid-cols-2 gap-3 sm:grid-cols-4">
          <div class="card p-4"><div class="text-xs text-mist">Haunted score</div><div class="text-2xl font-bold text-[#ff7a7a]">{{ w.hauntedScore }}</div></div>
          <div class="card p-4"><div class="text-xs text-mist">Rank</div><div class="text-2xl font-bold text-bone">#{{ w.rank }}</div></div>
          <div class="card p-4"><div class="text-xs text-mist">Open spots</div><div class="text-2xl font-bold text-bone">{{ w.openSpots }}</div></div>
          <div class="card p-4"><div class="text-xs text-mist">Cursed</div><div class="text-2xl font-bold text-[#ff7a7a]">{{ w.cursedSpots }}</div></div>
        </div>
      }
      <section class="card" aria-label="Open spots in your ward">
        <ul class="divide-y divide-white/5">
          @for (s of wardSpots(); track s.id) {
            <li class="flex items-center gap-4 py-3">
              <app-monster-avatar [emoji]="s.monsterEmoji" [level]="s.monsterLevel" [cursed]="s.cursed" [scale]="0.8" />
              <div class="min-w-0 flex-1">
                <p class="font-semibold text-bone">{{ s.monsterName }} <span class="text-sm font-normal text-mist">· Level {{ s.monsterLevel }} {{ levelTitle(s.monsterLevel) }}, {{ s.ageDays }} {{ s.ageDays === 1 ? 'day' : 'days' }}</span></p>
                <p class="truncate text-sm text-mist">{{ s.description }} · {{ s.linkedEntityName ?? 'Ward responsibility' }} · reported by {{ s.reportedByName }}</p>
              </div>
              <a [routerLink]="['/spots', s.id]" class="btn-primary shrink-0 text-sm no-underline hover:no-underline"
                 [attr.aria-label]="'Review ' + s.monsterName + ': ' + (s.description || '')">Review</a>
            </li>
          } @empty {
            <li class="py-6 text-center text-mist">No open spots in your ward. Ghost free!</li>
          }
        </ul>
      </section>
    }
    }
  `,
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly toast = inject(ToastService);
  readonly auth = inject(AuthService);

  readonly dashboard = signal<Dashboard | null>(null);
  readonly wardSpots = signal<Spot[]>([]);
  readonly wardScores = signal<WardScore[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly jobBusy = signal(false);
  readonly typeLabels = ENTITY_TYPE_LABELS;
  readonly ward = computed(() => this.auth.user()?.ward ?? '');
  readonly wardScore = computed(() =>
    this.wardScores().find((w) => w.ward.toLowerCase() === this.ward().toLowerCase()) ?? null);

  ngOnInit(): void {
    const user = this.auth.user();
    if (user?.entityId) {
      this.api.dashboard(user.entityId).subscribe({
        next: (d) => this.done(() => this.dashboard.set(d)),
        error: (e) => this.fail(e),
      });
    } else if (user?.role === 'WARD_OFFICER') {
      this.loadWard();
    } else {
      this.loading.set(false);
      this.error.set('Dashboards are for entity admins and ward officers.');
    }
  }

  levelTitle(level: number): string {
    return levelInfo(level).title;
  }

  runJob(): void {
    this.jobBusy.set(true);
    this.api.runOverdueJob().subscribe({
      next: (r) => {
        this.jobBusy.set(false);
        this.toast.show(`Overdue job ran: ${r.penalisedSpots} newly overdue spot(s) penalised.`, 'info');
        this.loadWard();
      },
      error: (e) => {
        this.jobBusy.set(false);
        this.toast.error(e);
      },
    });
  }

  private loadWard(): void {
    this.api.wardScores().subscribe({ next: (w) => this.wardScores.set(w), error: () => {} });
    this.api.spots({ status: 'OPEN', ward: this.ward(), size: 100 }).subscribe({
      next: (p) => this.done(() => this.wardSpots.set([...p.content].sort((a, b) => b.monsterLevel - a.monsterLevel || b.ageDays - a.ageDays))),
      error: (e) => this.fail(e),
    });
  }

  private done(apply: () => void): void {
    apply();
    this.loading.set(false);
  }

  private fail(e: unknown): void {
    this.loading.set(false);
    this.error.set(errorMessage(e, 'Could not load the dashboard'));
  }
}
