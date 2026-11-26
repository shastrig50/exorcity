import { Component, computed, input } from '@angular/core';
import { RATING_STYLES } from '../core/monsters';
import { Rating } from '../core/models';

const ICONS: Record<Rating, string> = { GHOST_FREE: '✨', CALM_SPIRITS: '🕯️', HAUNTED: '👻', CURSED: '💀' };
const LABELS: Record<Rating, string> = {
  GHOST_FREE: 'Ghost Free', CALM_SPIRITS: 'Calm Spirits', HAUNTED: 'Haunted', CURSED: 'Cursed',
};

@Component({
  selector: 'app-rating-badge',
  standalone: true,
  template: `
    <span class="inline-flex items-center gap-1.5 rounded-full border bg-night/60 px-3 py-1 text-sm font-semibold"
          [class]="style().ring + ' ' + style().text">
      <span aria-hidden="true">{{ icon() }}</span>{{ label() }}
    </span>
  `,
})
export class RatingBadgeComponent {
  readonly rating = input.required<Rating>();
  readonly style = computed(() => RATING_STYLES[this.rating()]);
  readonly icon = computed(() => ICONS[this.rating()]);
  readonly label = computed(() => LABELS[this.rating()]);
}
