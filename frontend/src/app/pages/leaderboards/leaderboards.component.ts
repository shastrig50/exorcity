import { Component, OnInit, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { RATING_STYLES } from '../../core/monsters';
import { CitizenEntry, EntityEntry, Rating, WardScore } from '../../core/models';
import { errorMessage } from '../../core/toast.service';
import { CandleLoaderComponent } from '../../shared/candle-loader.component';

@Component({
  selector: 'app-leaderboards',
  standalone: true,
  imports: [CandleLoaderComponent],
  template: `
    <h1 class="text-4xl text-pumpkin">Leaderboards</h1>
    <p class="mb-6 text-mist">Who keeps the spirits calm, and where the city is most haunted.</p>

    @if (loading()) {
      <app-candle-loader />
    } @else if (error()) {
      <div class="card" role="alert">{{ error() }}</div>
    } @else {
      <div class="grid gap-6 lg:grid-cols-2">
        <section class="card" aria-labelledby="lb-hunters">
          <h2 id="lb-hunters" class="mb-3 text-xl font-bold text-bone"><span aria-hidden="true">🔦</span> Top Ghost Hunters</h2>
          <ol class="divide-y divide-white/5">
            @for (c of citizens(); track c.id) {
              <li class="flex items-center gap-3 py-2" [class.bg-night]="c.id === auth.user()?.id">
                <span class="w-8 text-center font-bold text-mist">{{ medal(c.rank) }}</span>
                <span class="flex-1">
                  <span class="block font-semibold text-bone">{{ c.name }}{{ c.id === auth.user()?.id ? ' (you)' : '' }}</span>
                  <span class="block text-xs text-mist">{{ c.reports }} reports · {{ c.exorcisms }} exorcisms · {{ c.rewardPoints }} reward pts</span>
                </span>
                <span class="text-right">
                  <span class="block text-lg font-bold tabular-nums" [class]="ratingText(c.rating)">{{ c.civicScore }}</span>
                  <span class="block text-xs text-mist">{{ c.ratingLabel }}</span>
                </span>
              </li>
            }
          </ol>
        </section>

        <section class="card" aria-labelledby="lb-wards">
          <h2 id="lb-wards" class="mb-1 text-xl font-bold text-bone"><span aria-hidden="true">☠️</span> Most Haunted Wards</h2>
          <p class="mb-3 text-xs text-mist">Haunted score = sum of open monster levels + 2 per cursed spot</p>
          <ol class="space-y-3">
            @for (w of wards(); track w.ward) {
              <li>
                <div class="flex items-baseline justify-between gap-2">
                  <span class="font-semibold text-bone">{{ w.rank }}. {{ w.ward }}</span>
                  <span class="text-sm text-mist">{{ w.openSpots }} open · {{ w.cursedSpots }} cursed ·
                    <span class="font-bold text-[#ff7a7a]">{{ w.hauntedScore }}</span></span>
                </div>
                <div class="mt-1 h-2 overflow-hidden rounded-full bg-night" aria-hidden="true">
                  <div class="h-full rounded-full bg-gradient-to-r from-pumpkin to-blood" [style.width.%]="wardPct(w)"></div>
                </div>
              </li>
            }
          </ol>
        </section>

        <section class="card" aria-labelledby="lb-societies">
          <h2 id="lb-societies" class="mb-3 text-xl font-bold text-bone"><span aria-hidden="true">🏘️</span> Cleanest Societies</h2>
          <ol class="divide-y divide-white/5">
            @for (e of societies(); track e.id) {
              <li class="flex items-center gap-3 py-2">
                <span class="w-8 text-center font-bold text-mist">{{ medal(e.rank) }}</span>
                <span class="flex-1"><span class="block font-semibold text-bone">{{ e.name }}</span>
                  <span class="block text-xs text-mist">{{ e.ward }} · {{ e.openSpots }} open spot(s)</span></span>
                <span class="text-right"><span class="block text-lg font-bold tabular-nums" [class]="ratingText(e.rating)">{{ e.civicScore }}</span>
                  <span class="block text-xs text-mist">{{ e.ratingLabel }}</span></span>
              </li>
            }
          </ol>
        </section>

        <section class="card" aria-labelledby="lb-corporate">
          <h2 id="lb-corporate" class="mb-3 text-xl font-bold text-bone"><span aria-hidden="true">🏢</span> Best Corporate Adopters</h2>
          <ol class="divide-y divide-white/5">
            @for (e of corporates(); track e.id) {
              <li class="flex items-center gap-3 py-2">
                <span class="w-8 text-center font-bold text-mist">{{ medal(e.rank) }}</span>
                <span class="flex-1"><span class="block font-semibold text-bone">{{ e.name }}</span>
                  <span class="block text-xs text-mist">{{ e.ward }} · {{ e.openSpots }} open spot(s)</span></span>
                <span class="text-right"><span class="block text-lg font-bold tabular-nums" [class]="ratingText(e.rating)">{{ e.civicScore }}</span>
                  <span class="block text-xs text-mist">{{ e.ratingLabel }}</span></span>
              </li>
            }
          </ol>
          <h3 class="mb-2 mt-6 font-semibold text-bone"><span aria-hidden="true">🛒</span> Shops</h3>
          <ol class="divide-y divide-white/5">
            @for (e of shops(); track e.id) {
              <li class="flex items-center gap-3 py-2">
                <span class="w-8 text-center font-bold text-mist">{{ medal(e.rank) }}</span>
                <span class="flex-1"><span class="block font-semibold text-bone">{{ e.name }}</span>
                  <span class="block text-xs text-mist">{{ e.ward }}</span></span>
                <span class="text-lg font-bold tabular-nums" [class]="ratingText(e.rating)">{{ e.civicScore }}</span>
              </li>
            }
          </ol>
        </section>
      </div>
    }
  `,
})
export class LeaderboardsComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly auth = inject(AuthService);
  readonly citizens = signal<CitizenEntry[]>([]);
  readonly societies = signal<EntityEntry[]>([]);
  readonly corporates = signal<EntityEntry[]>([]);
  readonly shops = signal<EntityEntry[]>([]);
  readonly wards = signal<WardScore[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    forkJoin({
      citizens: this.api.citizenLeaderboard(10),
      societies: this.api.entityLeaderboard('SOCIETY'),
      corporates: this.api.entityLeaderboard('CORPORATE_CAMPUS'),
      shops: this.api.entityLeaderboard('SHOP'),
      wards: this.api.wardScores(),
    }).subscribe({
      next: (r) => {
        this.citizens.set(r.citizens.content);
        this.societies.set(r.societies.content);
        this.corporates.set(r.corporates.content);
        this.shops.set(r.shops.content);
        this.wards.set(r.wards);
        this.loading.set(false);
      },
      error: (e) => {
        this.loading.set(false);
        this.error.set(errorMessage(e, 'Could not load leaderboards'));
      },
    });
  }

  medal(rank: number): string {
    return rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : `#${rank}`;
  }

  ratingText(r: Rating): string {
    return RATING_STYLES[r].text;
  }

  wardPct(w: WardScore): number {
    const max = Math.max(1, ...this.wards().map((x) => x.hauntedScore));
    return (w.hauntedScore / max) * 100;
  }
}
