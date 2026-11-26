import { Component, inject } from '@angular/core';
import { ToastService } from '../core/toast.service';

@Component({
  selector: 'app-toast-outlet',
  standalone: true,
  template: `
    <div class="pointer-events-none fixed inset-x-0 bottom-4 z-[3000] flex flex-col items-center gap-2 px-4"
         aria-live="polite" aria-atomic="false">
      @for (t of toasts.toasts(); track t.id) {
        <div class="pointer-events-auto flex w-full max-w-md items-start gap-3 rounded-lg border px-4 py-3 shadow-xl"
             [class]="t.kind === 'success' ? 'border-ecto/50 bg-crypt' : t.kind === 'error' ? 'border-blood bg-[#2a0d0d]' : 'border-pumpkin/40 bg-crypt'"
             [attr.role]="t.kind === 'error' ? 'alert' : 'status'">
          <span aria-hidden="true">{{ t.kind === 'success' ? '✨' : t.kind === 'error' ? '💀' : '🎃' }}</span>
          <p class="flex-1 text-sm text-bone">{{ t.message }}</p>
          <button type="button" class="text-mist hover:text-bone" (click)="toasts.dismiss(t.id)" aria-label="Dismiss notification">✕</button>
        </div>
      }
    </div>
  `,
})
export class ToastOutletComponent {
  readonly toasts = inject(ToastService);
}
