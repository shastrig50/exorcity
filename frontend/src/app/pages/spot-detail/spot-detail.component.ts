import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { levelInfo } from '../../core/monsters';
import { Spot } from '../../core/models';
import { ToastService, errorMessage } from '../../core/toast.service';
import { BeforeAfterSliderComponent } from '../../shared/before-after-slider.component';
import { CandleLoaderComponent } from '../../shared/candle-loader.component';
import { MonsterAvatarComponent } from '../../shared/monster-avatar.component';
import { PhotoInputComponent } from '../../shared/photo-input.component';
import { TimeAgoPipe } from '../../shared/time-ago.pipe';

@Component({
  selector: 'app-spot-detail',
  standalone: true,
  imports: [RouterLink, DatePipe, TimeAgoPipe, BeforeAfterSliderComponent, CandleLoaderComponent,
    MonsterAvatarComponent, PhotoInputComponent],
  template: `
    <div class="mb-4 flex flex-wrap gap-4 text-sm">
      <a routerLink="/map">← Back to the haunted map</a>
      <a routerLink="/map" [queryParams]="{ spot: id() }">📍 View on map</a>
    </div>

    @if (loading()) {
      <app-candle-loader />
    } @else {
    @if (spot(); as s) {
      <div class="grid gap-6 lg:grid-cols-[1fr_380px]">
        <section class="space-y-4">
          @if (s.status === 'CLEANED' && s.afterPhotoUrl) {
            <app-before-after-slider [before]="s.beforePhotoUrl" [after]="s.afterPhotoUrl" [caption]="s.description || s.monsterName" />
          } @else {
            <img [src]="s.beforePhotoUrl" [alt]="'Before photo: ' + (s.description || s.monsterName)"
                 class="aspect-[16/10] w-full rounded-xl border border-white/10 object-cover" />
          }
          @if (s.description) { <p class="text-lg text-bone">{{ s.description }}</p> }
          <dl class="card grid grid-cols-2 gap-x-4 gap-y-3 text-sm sm:grid-cols-3">
            <div><dt class="text-mist">Reported</dt><dd class="text-bone">{{ s.createdAt | timeAgo }}</dd></div>
            <div><dt class="text-mist">By</dt><dd class="text-bone">{{ s.reportedByName }}</dd></div>
            <div><dt class="text-mist">Ward</dt><dd class="text-bone">{{ s.ward ?? 'Unknown' }}</dd></div>
            <div><dt class="text-mist">Responsible</dt><dd class="text-bone">{{ s.linkedEntityName ?? 'Ward (no entity within 200 m)' }}</dd></div>
            <div><dt class="text-mist">Confirmations</dt><dd class="text-bone">{{ s.confirmationCount }}</dd></div>
            <div><dt class="text-mist">Location</dt><dd class="text-bone">{{ s.latitude.toFixed(4) }}, {{ s.longitude.toFixed(4) }}</dd></div>
            @if (s.cleanedAt) {
              <div><dt class="text-mist">Exorcised</dt><dd class="text-ecto">{{ s.cleanedAt | date: 'mediumDate' }}</dd></div>
              <div><dt class="text-mist">Exorcist</dt><dd class="text-bone">{{ s.cleanedByName }}</dd></div>
            }
          </dl>
        </section>

        <aside class="space-y-4">
          <section class="card text-center" aria-labelledby="monster-title">
            <div class="exorcise-stage my-4 min-h-[110px]" [class.exorcising]="exorcising()">
              @if (s.status === 'OPEN' || exorcising()) {
                <app-monster-avatar [emoji]="s.monsterEmoji" [level]="s.monsterLevel || 1" [cursed]="s.cursed" [scale]="1.6"
                                    [label]="s.monsterName + ' level ' + s.monsterLevel" />
              } @else if (s.status === 'CLEANED') {
                <span class="text-6xl" role="img" aria-label="Exorcised">✨</span>
              } @else {
                <span class="text-6xl" role="img" aria-label="Rejected report">🚫</span>
              }
              @if (exorcising()) {
                @for (p of puffs; track $index) {
                  <span class="smoke-puff" [style.--dx]="p.dx" [style.animation-delay]="p.delay" aria-hidden="true"></span>
                }
              }
            </div>
            <h1 id="monster-title" class="text-3xl text-pumpkin">{{ s.monsterName }}</h1>
            @if (s.status === 'OPEN') {
              <p class="mt-1 font-semibold text-bone">Level {{ s.monsterLevel }}: {{ levelTitle() }}</p>
              <p class="text-sm text-mist">{{ s.ageDays }} days uncleaned · {{ s.confirmationCount }} confirmations</p>
              @if (s.cursed) {
                <p class="mt-2 rounded-lg border border-blood bg-[#2a0d0d] px-3 py-2 text-sm text-bone">
                  ☠ <strong>Cursed.</strong> 3+ spots within 50 m in the last 30 days.
                </p>
              }
              <!-- progress to next level via confirmations -->
              @if (s.monsterLevel < 5) {
                <p class="mt-2 text-xs text-mist">{{ 5 - (s.confirmationCount % 5) }} more confirmation(s) will make it grow.</p>
              }
            } @else if (s.status === 'CLEANED') {
              <p class="mt-1 font-semibold text-ecto">Exorcised! This spot is clean.</p>
            } @else {
              <p class="mt-1 font-semibold text-mist">This report was rejected by the ward officer.</p>
            }
          </section>

          @if (s.status === 'OPEN') {
            <section class="card space-y-3" aria-label="Actions">
              @if (!auth.isLoggedIn()) {
                <p class="text-sm text-mist">Log in to confirm or exorcise this monster.</p>
                <a routerLink="/login" [queryParams]="{ next: '/spots/' + s.id }" class="btn-primary w-full no-underline hover:no-underline">Log in</a>
              } @else {
                @if (auth.isCitizen()) {
                  @if (isMine()) {
                    <p class="text-sm text-mist">You reported this one. Others need to confirm it.</p>
                  } @else if (s.confirmedByMe) {
                    <p class="text-sm text-ecto">✓ You confirmed this sighting.</p>
                  } @else {
                    <button type="button" class="btn-ghost w-full" (click)="confirm()" [disabled]="busy()">
                      👀 I see it too: confirm (+5)
                    </button>
                  }
                }

                @if (auth.canClean(s)) {
                  @if (!showClean()) {
                    <button type="button" class="btn-primary w-full" (click)="showClean.set(true)">✨ Exorcise this spot (+25)</button>
                  } @else {
                    <div class="space-y-3">
                      <app-photo-input legend="After photo" idPrefix="after" [(url)]="afterUrl" sample="/seed/exorcised.svg" />
                      <div class="flex gap-2">
                        <button type="button" class="btn-ghost flex-1" (click)="showClean.set(false)">Cancel</button>
                        <button type="button" class="btn-primary flex-1" (click)="clean()" [disabled]="busy() || !afterUrl()">Exorcise!</button>
                      </div>
                    </div>
                  }
                }

                @if (canReject()) {
                  <button type="button" class="inline-flex min-h-[44px] w-full items-center justify-center rounded-lg border border-blood px-4 text-bone hover:bg-blood"
                          (click)="reject()" [disabled]="busy()">
                    🚫 Reject as fake (reporter −30)
                  </button>
                }
              }
              @if (actionError()) { <p class="text-sm text-[#ff8a8a]" role="alert">{{ actionError() }}</p> }
            </section>
          }
        </aside>
      </div>
    } @else {
      <div class="card" role="alert">{{ error() || 'This spot could not be found.' }}</div>
    }
    }
  `,
})
export class SpotDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly toast = inject(ToastService);
  readonly auth = inject(AuthService);

  /** Route param :id (component input binding). */
  readonly id = input.required<string>();
  readonly spot = signal<Spot | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly busy = signal(false);
  readonly actionError = signal('');
  readonly showClean = signal(false);
  readonly afterUrl = signal('');
  readonly exorcising = signal(false);
  readonly puffs = [
    { dx: '-50px', delay: '0ms' }, { dx: '40px', delay: '120ms' }, { dx: '-10px', delay: '240ms' },
    { dx: '60px', delay: '360ms' }, { dx: '-70px', delay: '420ms' }, { dx: '15px', delay: '520ms' },
  ];

  readonly isMine = computed(() => this.spot()?.reportedById === this.auth.user()?.id);
  readonly levelTitle = computed(() => levelInfo(this.spot()?.monsterLevel ?? 1).title);
  readonly canReject = computed(() => {
    const s = this.spot();
    const u = this.auth.user();
    return !!s && !!u && u.role === 'WARD_OFFICER' && !!u.ward && u.ward.toLowerCase() === (s.ward ?? '').toLowerCase();
  });

  ngOnInit(): void {
    this.api.spot(+this.id()).subscribe({
      next: (s) => {
        this.spot.set(s);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(errorMessage(e, 'Could not load this spot'));
        this.loading.set(false);
      },
    });
  }

  confirm(): void {
    this.run(this.api.confirm(this.spot()!.id), (r) => {
      this.spot.set(r.spot);
      this.toast.success(`Sighting confirmed. +${r.pointsEarned} Civic Score.`);
    });
  }

  clean(): void {
    if (!this.afterUrl()) {
      this.actionError.set('An after photo is required to exorcise a spot.');
      return;
    }
    this.run(this.api.clean(this.spot()!.id, this.afterUrl()), (r) => {
      this.exorcising.set(true);
      this.showClean.set(false);
      // Let the smoke play before the cleaned state replaces the monster.
      setTimeout(() => {
        this.exorcising.set(false);
        this.spot.set(r.spot);
      }, 1300);
      this.toast.success(`Exorcised! +${r.pointsEarned} Civic Score. The ${r.spot.monsterName} is gone.`);
    });
  }

  reject(): void {
    this.run(this.api.reject(this.spot()!.id), (r) => {
      this.spot.set(r.spot);
      this.toast.show('Report rejected. The reporter loses 30 points.', 'info');
    });
  }

  private run<T extends { newCivicScore: number }>(call: Observable<T>, done: (r: T) => void): void {
    this.busy.set(true);
    this.actionError.set('');
    call.subscribe({
      next: (r) => {
        this.busy.set(false);
        this.auth.updateScore(r.newCivicScore);
        done(r);
      },
      error: (e) => {
        this.busy.set(false);
        this.actionError.set(errorMessage(e));
      },
    });
  }
}
