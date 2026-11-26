import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import * as L from 'leaflet';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { LEVELS, MONSTERS, levelInfo, spotAriaLabel } from '../../core/monsters';
import { GarbageType, Spot } from '../../core/models';
import { errorMessage } from '../../core/toast.service';
import { CandleLoaderComponent } from '../../shared/candle-loader.component';
import { MonsterAvatarComponent } from '../../shared/monster-avatar.component';

const BENGALURU: L.LatLngExpression = [12.975, 77.63];

type SortKey = 'level' | 'age' | 'confirmations';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [RouterLink, CandleLoaderComponent, MonsterAvatarComponent],
  template: `
    <div class="mb-4 flex flex-wrap items-end justify-between gap-3">
      <div>
        <h1 class="text-4xl text-pumpkin">The Haunted Map</h1>
        <p class="text-mist">
          <span class="font-semibold text-bone">{{ visible().length }}</span> monsters on the loose ·
          <span class="font-semibold text-[#ff7a7a]">{{ cursedCount() }}</span> cursed
        </p>
      </div>
      <div class="flex flex-wrap gap-2">
        @if (auth.isCitizen()) {
          <a routerLink="/report" class="btn-primary no-underline hover:no-underline">+ Report garbage</a>
        }
        <button type="button" class="btn-ghost" [attr.aria-pressed]="view() === 'list'" (click)="toggleView()">
          {{ view() === 'map' ? '☰ List view' : '🗺️ Map view' }}
        </button>
      </div>
    </div>

    <fieldset class="mb-4 flex flex-wrap gap-2" aria-label="Filter monsters by type">
      @for (m of monsters; track m.type) {
        <label class="flex min-h-[44px] cursor-pointer items-center gap-2 rounded-full border px-3 text-sm transition"
               [class]="isOn(m.type) ? 'border-pumpkin bg-crypt text-bone' : 'border-white/10 text-mist'">
          <input type="checkbox" class="sr-only" [checked]="isOn(m.type)" (change)="toggleType(m.type)" />
          <span aria-hidden="true">{{ m.emoji }}</span>{{ m.name }}
          <span class="text-xs text-mist">({{ countOf(m.type) }})</span>
        </label>
      }
    </fieldset>

    @if (error()) {
      <div class="card border-blood text-bone" role="alert">{{ error() }}</div>
    }

    <div class="grid gap-4 lg:grid-cols-[1fr_340px]">
      <div class="relative">
        @if (loading()) {
          <div class="absolute inset-0 z-[500] flex items-center justify-center bg-night/70">
            <app-candle-loader label="Summoning monsters…" />
          </div>
        }
        <div #mapEl class="haunted-map h-[62vh] min-h-[380px] w-full rounded-xl border border-white/10"
             [class.hidden]="view() === 'list'"
             role="region" aria-label="Map of garbage monsters in Bengaluru. Use Tab to move between monsters and Enter to open one."></div>

        @if (view() === 'list') {
          <div class="card overflow-x-auto p-0">
            <table class="w-full text-left text-sm">
              <caption class="sr-only">All open garbage spots, sortable</caption>
              <thead class="border-b border-white/10 text-mist">
                <tr>
                  <th scope="col" class="px-4 py-3">Monster</th>
                  <th scope="col" class="px-4 py-3" [attr.aria-sort]="sortKey() === 'level' ? 'descending' : 'none'">
                    <button type="button" class="font-semibold hover:text-pumpkin" (click)="sortKey.set('level')">Level</button>
                  </th>
                  <th scope="col" class="px-4 py-3" [attr.aria-sort]="sortKey() === 'age' ? 'descending' : 'none'">
                    <button type="button" class="font-semibold hover:text-pumpkin" (click)="sortKey.set('age')">Age</button>
                  </th>
                  <th scope="col" class="px-4 py-3" [attr.aria-sort]="sortKey() === 'confirmations' ? 'descending' : 'none'">
                    <button type="button" class="font-semibold hover:text-pumpkin" (click)="sortKey.set('confirmations')">Confirms</button>
                  </th>
                  <th scope="col" class="px-4 py-3">Ward / owner</th>
                </tr>
              </thead>
              <tbody>
                @for (s of sorted(); track s.id) {
                  <tr class="border-b border-white/5 hover:bg-night/50">
                    <td class="px-4 py-2">
                      <a [routerLink]="['/spots', s.id]" class="flex items-center gap-3 text-bone no-underline">
                        <app-monster-avatar [emoji]="s.monsterEmoji" [level]="s.monsterLevel" [cursed]="s.cursed" [scale]="0.8" />
                        <span><span class="font-semibold">{{ s.monsterName }}</span>
                          @if (s.cursed) { <span class="ml-1 text-xs font-bold text-[#ff7a7a]">CURSED</span> }</span>
                      </a>
                    </td>
                    <td class="px-4 py-2">{{ s.monsterLevel }} · {{ levelTitle(s.monsterLevel) }}</td>
                    <td class="px-4 py-2">{{ s.ageDays }} d</td>
                    <td class="px-4 py-2">{{ s.confirmationCount }}</td>
                    <td class="px-4 py-2 text-mist">{{ s.ward }}{{ s.linkedEntityName ? ' · ' + s.linkedEntityName : '' }}</td>
                  </tr>
                } @empty {
                  <tr><td colspan="5" class="px-4 py-6 text-center text-mist">No monsters match. The city rests… for now.</td></tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>

      <aside class="space-y-4" aria-label="Selected monster and legend">
        @if (selected(); as s) {
          <section class="card" aria-live="polite" aria-labelledby="sel-title">
            <div class="flex items-start gap-4">
              <app-monster-avatar [emoji]="s.monsterEmoji" [level]="s.monsterLevel" [cursed]="s.cursed" />
              <div class="min-w-0 flex-1">
                <h2 id="sel-title" class="text-lg font-bold text-bone">{{ s.monsterName }}</h2>
                <p class="text-sm text-mist">Level {{ s.monsterLevel }} · {{ levelTitle(s.monsterLevel) }} · {{ s.ageDays }} days old</p>
                @if (s.cursed) { <p class="mt-1 text-sm font-semibold text-[#ff7a7a]">☠ Cursed cluster</p> }
              </div>
              <button type="button" class="text-mist hover:text-bone" (click)="selected.set(null)" aria-label="Close details">✕</button>
            </div>
            <img [src]="s.beforePhotoUrl" [alt]="'Photo of ' + (s.description || s.monsterName)" class="mt-4 aspect-video w-full rounded-lg object-cover" />
            @if (s.description) { <p class="mt-3 text-sm text-bone">{{ s.description }}</p> }
            <dl class="mt-3 grid grid-cols-2 gap-2 text-sm">
              <dt class="text-mist">Responsible</dt><dd class="text-bone">{{ s.linkedEntityName ?? (s.ward ? s.ward + ' ward' : 'Ward') }}</dd>
              <dt class="text-mist">Confirmations</dt><dd class="text-bone">{{ s.confirmationCount }}</dd>
            </dl>
            <a [routerLink]="['/spots', s.id]" class="btn-primary mt-4 w-full no-underline hover:no-underline">Details, confirm &amp; exorcise</a>
          </section>
        } @else {
          <section class="card text-sm text-mist">
            <p><span aria-hidden="true">👆</span> Select a monster to see what it's hiding.</p>
          </section>
        }

        <section class="card" aria-labelledby="legend-title">
          <h2 id="legend-title" class="mb-3 font-semibold text-bone">Legend: scarier means more urgent</h2>
          <ul class="space-y-2 text-sm">
            @for (l of levels; track l.level) {
              <li class="flex items-center gap-3">
                <span class="flex w-16 justify-center"><app-monster-avatar emoji="🧟" [level]="l.level" [scale]="0.75" /></span>
                <span><span class="font-semibold text-bone">Level {{ l.level }}</span> <span class="text-mist">{{ l.title }}, {{ l.range }}</span></span>
              </li>
            }
            <li class="flex items-center gap-3">
              <span class="flex w-16 justify-center"><app-monster-avatar emoji="👻" [level]="2" [cursed]="true" [scale]="0.75" /></span>
              <span><span class="font-semibold text-[#ff7a7a]">Red aura</span> <span class="text-mist">cursed: 3+ spots within 50 m</span></span>
            </li>
          </ul>
          <p class="mt-3 text-xs text-mist">+1 level for every 5 confirmations.</p>
        </section>
      </aside>
    </div>
  `,
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private readonly api = inject(ApiService);
  private readonly zone = inject(NgZone);
  readonly auth = inject(AuthService);
  private readonly mapEl = viewChild.required<ElementRef<HTMLDivElement>>('mapEl');

  readonly monsters = MONSTERS;
  readonly levels = LEVELS;
  readonly spots = signal<Spot[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly selected = signal<Spot | null>(null);
  readonly view = signal<'map' | 'list'>('map');
  readonly sortKey = signal<SortKey>('level');
  readonly enabledTypes = signal<Set<GarbageType>>(new Set(MONSTERS.map((m) => m.type)));

  readonly visible = computed(() => this.spots().filter((s) => this.enabledTypes().has(s.type)));
  readonly cursedCount = computed(() => this.visible().filter((s) => s.cursed).length);
  readonly sorted = computed(() => {
    const key = this.sortKey();
    return [...this.visible()].sort((a, b) =>
      key === 'age' ? b.ageDays - a.ageDays
        : key === 'confirmations' ? b.confirmationCount - a.confirmationCount
          : b.monsterLevel - a.monsterLevel || b.ageDays - a.ageDays);
  });

  private map?: L.Map;
  private layer = L.layerGroup();

  ngAfterViewInit(): void {
    this.zone.runOutsideAngular(() => {
      this.map = L.map(this.mapEl().nativeElement, { zoomControl: true, minZoom: 10, maxZoom: 18 }).setView(BENGALURU, 12);
      L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      }).addTo(this.map);
      this.layer.addTo(this.map);
    });
    this.load();
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  load(): void {
    this.loading.set(true);
    this.api.spots({ status: 'OPEN', size: 100 }).subscribe({
      next: (page) => {
        this.spots.set(page.content);
        this.loading.set(false);
        this.renderMarkers();
      },
      error: (e) => {
        this.loading.set(false);
        this.error.set(errorMessage(e, 'Could not load the monsters'));
      },
    });
  }

  toggleView(): void {
    this.view.update((v) => (v === 'map' ? 'list' : 'map'));
    if (this.view() === 'map') setTimeout(() => this.map?.invalidateSize());
  }

  toggleType(type: GarbageType): void {
    this.enabledTypes.update((set) => {
      const next = new Set(set);
      next.has(type) ? next.delete(type) : next.add(type);
      return next;
    });
    this.renderMarkers();
  }

  isOn(type: GarbageType): boolean {
    return this.enabledTypes().has(type);
  }

  countOf(type: GarbageType): number {
    return this.spots().filter((s) => s.type === type).length;
  }

  levelTitle(level: number): string {
    return levelInfo(level).title;
  }

  private renderMarkers(): void {
    if (!this.map) return;
    const spots = this.visible();
    this.zone.runOutsideAngular(() => {
      this.layer.clearLayers();
      // Smaller monsters first so the scariest render on top.
      [...spots].sort((a, b) => a.monsterLevel - b.monsterLevel).forEach((s) => {
        const size = levelInfo(s.monsterLevel).size;
        const icon = L.divIcon({
          className: 'monster-marker',
          html: `<span class="monster lvl-${s.monsterLevel}${s.cursed ? ' cursed' : ''}"><span class="monster-emoji">${s.monsterEmoji}</span></span>`,
          iconSize: [size, size],
          iconAnchor: [size / 2, size / 2],
        });
        const marker = L.marker([s.latitude, s.longitude], {
          icon,
          keyboard: true,
          title: spotAriaLabel(s),
          zIndexOffset: s.monsterLevel * 100 + (s.cursed ? 50 : 0),
          riseOnHover: true,
        });
        marker.on('click', () => this.zone.run(() => this.selected.set(s)));
        marker.on('add', () => {
          const el = marker.getElement();
          el?.setAttribute('aria-label', spotAriaLabel(s));
          el?.setAttribute('role', 'button');
        });
        marker.addTo(this.layer);
      });
    });
  }
}
