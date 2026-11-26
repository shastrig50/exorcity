import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, inject, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { ApiService } from '../../core/api.service';
import { MONSTERS } from '../../core/monsters';
import { GarbageType } from '../../core/models';
import { ToastService, errorMessage } from '../../core/toast.service';
import { PhotoInputComponent } from '../../shared/photo-input.component';

const SAMPLE_PHOTOS: Record<GarbageType, string> = {
  ROADSIDE_PILE: '/seed/roadside-pile.svg',
  OVERFLOWING_BIN: '/seed/overflowing-bin.svg',
  PLASTIC_WASTE: '/seed/plastic-waste.svg',
  ILLEGAL_DUMP: '/seed/illegal-dump.svg',
  LAKE_OR_DRAIN: '/seed/lake-or-drain.svg',
};

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [FormsModule, PhotoInputComponent],
  template: `
    <h1 class="text-4xl text-pumpkin">Report garbage</h1>
    <p class="mb-6 text-mist">Every report summons a monster on the map. The responsible society, campus or shop within 200 m is tagged automatically.</p>

    <form class="grid gap-6 lg:grid-cols-2" (ngSubmit)="submit()" novalidate>
      <div class="space-y-6">
        <fieldset class="card">
          <legend class="mb-3 font-semibold text-bone">What did you find? <span class="text-pumpkin" aria-hidden="true">*</span></legend>
          <div class="grid grid-cols-1 gap-2 sm:grid-cols-2" role="radiogroup">
            @for (m of monsters; track m.type) {
              <label class="flex min-h-[52px] cursor-pointer items-center gap-3 rounded-lg border px-3 py-2 transition"
                     [class]="type() === m.type ? 'border-pumpkin bg-night' : 'border-white/10 hover:border-white/30'">
                <input type="radio" name="type" class="accent-pumpkin" [value]="m.type" [checked]="type() === m.type"
                       (change)="type.set(m.type)" />
                <span class="text-2xl" aria-hidden="true">{{ m.emoji }}</span>
                <span><span class="block font-semibold text-bone">{{ m.label }}</span>
                  <span class="block text-xs text-mist">{{ m.name }}</span></span>
              </label>
            }
          </div>
        </fieldset>

        <div class="card">
          <label for="description" class="mb-1 block font-semibold text-bone">Description</label>
          <textarea id="description" name="description" rows="3" maxlength="1000" [(ngModel)]="description"
                    placeholder="e.g. Bags of waste dumped next to the bus stop"
                    class="w-full rounded-lg border border-white/10 bg-night px-3 py-2 text-bone placeholder:text-mist/60"></textarea>
        </div>

        <div class="card">
          <app-photo-input legend="Before photo" idPrefix="before" [(url)]="photoUrl" [sample]="samplePhoto()" />
        </div>
      </div>

      <fieldset class="card space-y-3">
        <legend class="font-semibold text-bone">Where is it? <span class="text-pumpkin" aria-hidden="true">*</span></legend>
        <p class="text-sm text-mist" id="loc-hint">Use your GPS or click/tap the map to drop a pin.</p>
        <button type="button" class="btn-ghost" (click)="useGps()" [disabled]="locating()">
          <span aria-hidden="true">📍</span>{{ locating() ? 'Finding you…' : 'Use my location' }}
        </button>
        <div #mapEl class="haunted-map h-80 w-full rounded-lg border border-white/10" role="application"
             aria-label="Map for choosing the garbage location" aria-describedby="loc-hint"></div>
        <p class="text-sm" aria-live="polite">
          @if (lat() !== null) {
            <span class="text-bone">Pinned at {{ lat()!.toFixed(5) }}, {{ lng()!.toFixed(5) }}</span>
          } @else {
            <span class="text-mist">No location yet.</span>
          }
        </p>
        @if (error()) { <p class="text-sm text-[#ff8a8a]" role="alert">{{ error() }}</p> }
        <button type="submit" class="btn-primary w-full text-lg" [disabled]="busy()">
          {{ busy() ? 'Summoning…' : 'Summon the monster' }}
        </button>
      </fieldset>
    </form>
  `,
})
export class ReportComponent implements AfterViewInit, OnDestroy {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);
  private readonly zone = inject(NgZone);
  private readonly mapEl = viewChild.required<ElementRef<HTMLDivElement>>('mapEl');

  readonly monsters = MONSTERS;
  readonly type = signal<GarbageType>('ROADSIDE_PILE');
  description = '';
  readonly photoUrl = signal('');
  readonly lat = signal<number | null>(null);
  readonly lng = signal<number | null>(null);
  readonly busy = signal(false);
  readonly locating = signal(false);
  readonly error = signal('');

  private map?: L.Map;
  private pin?: L.CircleMarker;

  samplePhoto(): string {
    return SAMPLE_PHOTOS[this.type()];
  }

  ngAfterViewInit(): void {
    this.zone.runOutsideAngular(() => {
      this.map = L.map(this.mapEl().nativeElement).setView([12.975, 77.63], 12);
      L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      }).addTo(this.map);
      this.map.on('click', (e: L.LeafletMouseEvent) => this.zone.run(() => this.setLocation(e.latlng.lat, e.latlng.lng)));
    });
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  useGps(): void {
    if (!('geolocation' in navigator)) {
      this.error.set('GPS is not available in this browser. Tap the map instead.');
      return;
    }
    this.locating.set(true);
    navigator.geolocation.getCurrentPosition(
      (pos) => this.zone.run(() => {
        this.locating.set(false);
        this.setLocation(pos.coords.latitude, pos.coords.longitude);
        this.map?.setView([pos.coords.latitude, pos.coords.longitude], 16);
      }),
      () => this.zone.run(() => {
        this.locating.set(false);
        this.error.set('Could not get your location. Tap the map instead.');
      }),
      { enableHighAccuracy: true, timeout: 10000 },
    );
  }

  private setLocation(lat: number, lng: number): void {
    this.lat.set(lat);
    this.lng.set(lng);
    this.error.set('');
    this.zone.runOutsideAngular(() => {
      if (!this.map) return;
      this.pin?.remove();
      this.pin = L.circleMarker([lat, lng], { radius: 10, color: '#ff6b00', fillColor: '#ff6b00', fillOpacity: 0.6 }).addTo(this.map);
    });
  }

  submit(): void {
    if (this.lat() === null || this.lng() === null) {
      this.error.set('Choose a location with GPS or by tapping the map.');
      return;
    }
    if (!this.photoUrl()) {
      this.error.set('Add a before photo (upload, URL, or the sample photo).');
      return;
    }
    this.busy.set(true);
    this.api.createSpot({
      type: this.type(),
      description: this.description.trim(),
      latitude: this.lat()!,
      longitude: this.lng()!,
      beforePhotoUrl: this.photoUrl(),
    }).subscribe({
      next: (spot) => {
        this.busy.set(false);
        const owner = spot.linkedEntityName ? ` Linked to ${spot.linkedEntityName}.` : ' No entity within 200 m, so the ward is responsible.';
        this.toast.success(`A ${spot.monsterName} has been summoned!${owner}${spot.cursed ? ' ☠ This area is now CURSED.' : ''}`);
        this.router.navigate(['/spots', spot.id]);
      },
      error: (e) => {
        this.busy.set(false);
        this.error.set(errorMessage(e, 'Could not submit the report'));
      },
    });
  }
}
