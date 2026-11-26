import { Injectable, effect, signal } from '@angular/core';

const STORAGE_KEY = 'exorcity.reduceMotion';

/**
 * Reduced-motion preference. Defaults to the OS setting (prefers-reduced-motion);
 * the in-app toggle overrides it and is remembered in localStorage.
 */
@Injectable({ providedIn: 'root' })
export class MotionService {
  readonly reduced = signal<boolean>(this.initial());

  constructor() {
    effect(() => {
      const on = this.reduced();
      document.documentElement.classList.toggle('reduce-motion', on);
      try {
        localStorage.setItem(STORAGE_KEY, String(on));
      } catch {
        /* storage unavailable: preference lasts for this session only */
      }
    });
  }

  toggle(): void {
    this.reduced.update((v) => !v);
  }

  private initial(): boolean {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved !== null) return saved === 'true';
    } catch {
      /* ignore */
    }
    return typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
  }
}
