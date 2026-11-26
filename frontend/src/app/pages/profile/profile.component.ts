import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ROLE_LABELS } from '../../core/monsters';
import { Me } from '../../core/models';
import { errorMessage } from '../../core/toast.service';
import { CandleLoaderComponent } from '../../shared/candle-loader.component';
import { RatingBadgeComponent } from '../../shared/rating-badge.component';
import { ScoreMeterComponent } from '../../shared/score-meter.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [DatePipe, RouterLink, CandleLoaderComponent, RatingBadgeComponent, ScoreMeterComponent],
  template: `
    @if (me(); as m) {
      <div class="grid gap-6 lg:grid-cols-[360px_1fr]">
        <section class="card text-center" aria-labelledby="profile-title">
          <h1 id="profile-title" class="text-4xl text-pumpkin">{{ m.name }}</h1>
          <p class="text-sm text-mist">{{ roleLabels[m.role] }}{{ m.entityName ? ' · ' + m.entityName : '' }}{{ m.ward ? ' · ' + m.ward + ' ward' : '' }}</p>
          <div class="mt-6"><app-score-meter [score]="m.civicScore" /></div>
          <div class="mt-4"><app-rating-badge [rating]="m.rating" /></div>
          <div class="mt-6 rounded-xl border border-ecto/30 bg-night px-4 py-4">
            <div class="text-3xl font-bold text-ecto tabular-nums">{{ m.rewardPoints }}</div>
            <div class="text-sm text-mist">redeemable reward points</div>
            <p class="mt-2 text-xs text-mist">1 point for every 10 Civic Score above 600. Demo only, no real payments.</p>
          </div>
          <dl class="mt-6 grid grid-cols-3 gap-2 text-center">
            <div><dt class="text-xs text-mist">Reports</dt><dd class="text-2xl font-bold text-bone">{{ m.stats.reports }}</dd></div>
            <div><dt class="text-xs text-mist">Confirms</dt><dd class="text-2xl font-bold text-bone">{{ m.stats.confirmations }}</dd></div>
            <div><dt class="text-xs text-mist">Exorcisms</dt><dd class="text-2xl font-bold text-bone">{{ m.stats.exorcisms }}</dd></div>
          </dl>
          @if (m.entityId || m.role === 'WARD_OFFICER') {
            <a routerLink="/dashboard" class="btn-ghost mt-6 w-full no-underline hover:no-underline">Open my dashboard</a>
          }
        </section>

        <section class="card" aria-labelledby="history-title">
          <h2 id="history-title" class="mb-4 text-xl font-bold text-bone">Score history</h2>
          <div class="mb-4 grid gap-2 text-sm sm:grid-cols-2">
            <p class="text-mist"><span class="font-semibold text-ecto">750+</span> Ghost Free · <span class="font-semibold text-pumpkin">600–749</span> Calm Spirits</p>
            <p class="text-mist"><span class="font-semibold text-[#ff8a5c]">400–599</span> Haunted · <span class="font-semibold text-[#ff5a5a]">&lt;400</span> Cursed</p>
          </div>
          @if (m.history.length) {
            <ul class="divide-y divide-white/5">
              @for (e of m.history; track e.id) {
                <li class="flex items-center gap-4 py-3">
                  <span class="w-14 shrink-0 text-right text-lg font-bold tabular-nums"
                        [class]="e.points > 0 ? 'text-ecto' : e.points < 0 ? 'text-[#ff7a7a]' : 'text-mist'">
                    {{ e.points > 0 ? '+' : '' }}{{ e.points }}
                  </span>
                  <span class="flex-1">
                    <span class="block text-bone">{{ e.description }}</span>
                    <span class="block text-xs text-mist">{{ e.createdAt | date: 'medium' }}</span>
                  </span>
                  @if (e.spotId) { <a [routerLink]="['/spots', e.spotId]" class="text-sm" [attr.aria-label]="'View spot ' + e.spotId">View</a> }
                </li>
              }
            </ul>
          } @else {
            <p class="text-mist">No score events yet. Go hunt some monsters!</p>
          }
        </section>
      </div>
    } @else if (error()) {
      <div class="card" role="alert">{{ error() }}</div>
    } @else {
      <app-candle-loader />
    }
  `,
})
export class ProfileComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  readonly me = signal<Me | null>(null);
  readonly error = signal('');
  readonly roleLabels = ROLE_LABELS;

  ngOnInit(): void {
    this.api.me().subscribe({
      next: (m) => {
        this.me.set(m);
        this.auth.updateScore(m.civicScore);
      },
      error: (e) => this.error.set(errorMessage(e, 'Could not load your profile')),
    });
  }
}
