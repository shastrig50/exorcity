import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { ApiError } from './models';

export interface Toast {
  id: number;
  kind: 'success' | 'error' | 'info';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private nextId = 1;

  show(message: string, kind: Toast['kind'] = 'info', ms = 4500): void {
    const id = this.nextId++;
    this.toasts.update((t) => [...t, { id, kind, message }]);
    setTimeout(() => this.dismiss(id), ms);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(err: unknown, fallback = 'Something spooky went wrong'): void {
    this.show(errorMessage(err, fallback), 'error', 6000);
  }

  dismiss(id: number): void {
    this.toasts.update((t) => t.filter((x) => x.id !== id));
  }
}

export function errorMessage(err: unknown, fallback = 'Something spooky went wrong'): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) return 'Cannot reach the Exorcity server. Is the backend running?';
    const body = err.error as ApiError | null;
    if (body?.fieldErrors) {
      const first = Object.entries(body.fieldErrors)[0];
      if (first) return `${first[0]}: ${first[1]}`;
    }
    if (body?.message) return body.message;
  }
  return fallback;
}
