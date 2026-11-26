import { Component, computed, input } from '@angular/core';
import { RATING_STYLES, ratingFor } from '../core/monsters';

/** Semicircular glowing gauge for a 0–900 Civic Score. */
@Component({
  selector: 'app-score-meter',
  standalone: true,
  template: `
    <div class="relative mx-auto w-full max-w-xs" role="meter" aria-valuemin="0" aria-valuemax="900"
         [attr.aria-valuenow]="score()" [attr.aria-label]="'Civic Score ' + score() + ' out of 900'">
      <svg viewBox="0 0 200 115" class="w-full" aria-hidden="true">
        <path d="M 15 100 A 85 85 0 0 1 185 100" fill="none" stroke="#2a2a35" stroke-width="16" stroke-linecap="round" />
        <path d="M 15 100 A 85 85 0 0 1 185 100" fill="none" [attr.stroke]="color()" stroke-width="16"
              stroke-linecap="round" pathLength="100" [attr.stroke-dasharray]="pct() + ' 100'"
              class="score-arc" [style.filter]="'drop-shadow(0 0 8px ' + color() + ')'" />
        <!-- rating band ticks at 400 / 600 / 750 -->
        @for (t of ticks; track t) {
          <line [attr.x1]="tick(t, 74).x" [attr.y1]="tick(t, 74).y" [attr.x2]="tick(t, 96).x" [attr.y2]="tick(t, 96).y"
                stroke="#0b0b0f" stroke-width="2" />
        }
      </svg>
      <div class="absolute inset-x-0 bottom-1 text-center">
        <div class="text-5xl font-bold tabular-nums" [style.color]="color()">{{ score() }}</div>
        <div class="text-xs uppercase tracking-widest text-mist">of 900</div>
      </div>
    </div>
  `,
})
export class ScoreMeterComponent {
  readonly score = input.required<number>();
  readonly ticks = [400, 600, 750];
  readonly pct = computed(() => Math.max(0, Math.min(100, (this.score() / 900) * 100)));
  readonly color = computed(() => RATING_STYLES[ratingFor(this.score())].color);

  tick(value: number, r: number) {
    const angle = Math.PI * (1 - value / 900);
    return { x: 100 + r * Math.cos(angle), y: 100 - r * Math.sin(angle) };
  }
}
