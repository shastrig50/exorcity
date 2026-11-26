import { Component, input, signal } from '@angular/core';

/** Drag (or use arrow keys on) the range input to reveal the after photo over the before photo. */
@Component({
  selector: 'app-before-after-slider',
  standalone: true,
  template: `
    <figure class="overflow-hidden rounded-xl border border-white/10">
      <div class="relative aspect-[16/10] w-full select-none bg-night">
        <img [src]="after()" [alt]="'After cleaning: ' + caption()" class="absolute inset-0 h-full w-full object-cover" />
        <img [src]="before()" [alt]="'Before cleaning: ' + caption()"
             class="absolute inset-0 h-full w-full object-cover"
             [style.clip-path]="'inset(0 ' + (100 - pos()) + '% 0 0)'" />
        <div class="pointer-events-none absolute inset-y-0 w-0.5 bg-ecto shadow-[0_0_12px_#39ff14]" [style.left.%]="pos()"></div>
        <span class="absolute left-2 top-2 rounded bg-night/80 px-2 py-0.5 text-xs font-semibold text-bone">Before</span>
        <span class="absolute right-2 top-2 rounded bg-night/80 px-2 py-0.5 text-xs font-semibold text-ecto">After</span>
      </div>
      <label class="flex items-center gap-3 bg-crypt px-4 py-3 text-sm text-mist">
        <span class="shrink-0">Reveal</span>
        <input type="range" min="0" max="100" [value]="pos()" (input)="pos.set(+$any($event.target).value)"
               class="w-full accent-pumpkin" aria-label="Slide to compare the before and after photos" />
      </label>
    </figure>
  `,
})
export class BeforeAfterSliderComponent {
  readonly before = input.required<string>();
  readonly after = input.required<string>();
  readonly caption = input('garbage spot');
  readonly pos = signal(50);
}
