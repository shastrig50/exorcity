import { Component, input } from '@angular/core';

/** Flickering candle shown while data loads. */
@Component({
  selector: 'app-candle-loader',
  standalone: true,
  template: `
    <div class="flex flex-col items-center justify-center gap-3 py-10" role="status" aria-live="polite">
      <div class="candle" aria-hidden="true">
        <div class="candle-flame"></div>
        <div class="candle-body"></div>
      </div>
      <span class="text-sm text-mist">{{ label() }}</span>
    </div>
  `,
})
export class CandleLoaderComponent {
  readonly label = input('Summoning data…');
}
