import { Component, computed, input } from '@angular/core';
import { levelInfo } from '../core/monsters';

/** The monster bubble used on the map, in lists and on the detail page. Size/colour = urgency. */
@Component({
  selector: 'app-monster-avatar',
  standalone: true,
  template: `
    <span class="monster" [class]="'monster lvl-' + level() + (cursed() ? ' cursed' : '')"
          [style.width.px]="px()" [style.height.px]="px()" [style.font-size.px]="px() * 0.6"
          [attr.aria-hidden]="label() ? null : 'true'" [attr.role]="label() ? 'img' : null"
          [attr.aria-label]="label() || null">
      <span class="monster-emoji">{{ emoji() }}</span>
    </span>
  `,
})
export class MonsterAvatarComponent {
  readonly emoji = input.required<string>();
  readonly level = input(1);
  readonly cursed = input(false);
  readonly scale = input(1);
  readonly label = input<string>('');
  readonly px = computed(() => Math.round(levelInfo(this.level()).size * this.scale()));
}
