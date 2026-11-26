import { Component, OnInit, inject, input, model, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { errorMessage } from '../core/toast.service';

/**
 * Photo picker: uploads to Cloudinary when the backend has it configured,
 * otherwise accepts an image URL. A sample image keeps demos frictionless.
 */
@Component({
  selector: 'app-photo-input',
  standalone: true,
  imports: [FormsModule],
  template: `
    <fieldset class="space-y-3">
      <legend class="mb-1 font-semibold text-bone">{{ legend() }} <span class="text-pumpkin" aria-hidden="true">*</span></legend>

      @if (uploadsEnabled()) {
        <label class="block text-sm text-mist" [for]="idPrefix() + '-file'">Upload a photo (max 5 MB)</label>
        <input [id]="idPrefix() + '-file'" type="file" accept="image/*" capture="environment"
               class="block w-full text-sm text-mist file:mr-3 file:rounded-md file:border-0 file:bg-pumpkin file:px-4 file:py-2 file:font-semibold file:text-night"
               (change)="onFile($event)" [disabled]="uploading()" />
        @if (uploading()) { <p class="text-sm text-mist" role="status">Uploading…</p> }
      }

      <div>
        <label class="block text-sm text-mist" [for]="idPrefix() + '-url'">
          {{ uploadsEnabled() ? 'or paste an image URL' : 'Image URL' }}
        </label>
        <input [id]="idPrefix() + '-url'" type="url" inputmode="url" placeholder="https://…"
               class="mt-1 w-full rounded-lg border border-white/10 bg-night px-3 py-2 text-bone placeholder:text-mist/60"
               [ngModel]="url()" (ngModelChange)="url.set($event)" [attr.aria-describedby]="idPrefix() + '-hint'" />
        <p [id]="idPrefix() + '-hint'" class="mt-1 text-xs text-mist">
          No photo handy?
          <button type="button" class="font-semibold text-pumpkin underline-offset-4 hover:underline" (click)="url.set(sample())">
            Use a sample photo
          </button>
        </p>
      </div>

      @if (error()) { <p class="text-sm text-[#ff8a8a]" role="alert">{{ error() }}</p> }

      @if (url()) {
        <img [src]="url()" [alt]="'Preview of the ' + legend().toLowerCase()" class="max-h-48 rounded-lg border border-white/10 object-cover" />
      }
    </fieldset>
  `,
})
export class PhotoInputComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly url = model<string>('');
  readonly legend = input('Photo');
  readonly sample = input('/seed/exorcised.svg');
  readonly idPrefix = input('photo');
  readonly uploadsEnabled = signal(false);
  readonly uploading = signal(false);
  readonly error = signal('');

  ngOnInit(): void {
    this.api.config().subscribe({ next: (c) => this.uploadsEnabled.set(c.photoUploadsEnabled), error: () => {} });
  }

  onFile(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.error.set('');
    this.uploading.set(true);
    this.api.uploadPhoto(file).subscribe({
      next: (r) => {
        this.url.set(r.url);
        this.uploading.set(false);
      },
      error: (e) => {
        this.error.set(errorMessage(e, 'Upload failed'));
        this.uploading.set(false);
      },
    });
  }
}
