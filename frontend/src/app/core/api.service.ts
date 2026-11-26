import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AuthResponse, CitizenEntry, CreateSpotRequest, Dashboard, EntityEntry, EntitySummary, EntityType, Me, Page,
  Spot, SpotAction, SpotStats, SpotStatus, WardScore,
} from './models';

export interface SpotQuery {
  status?: SpotStatus;
  ward?: string;
  bbox?: string;
  page?: number;
  size?: number;
}

/** Typed wrapper around the Exorcity REST API. */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/login`, { email, password });
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/register`, { name, email, password });
  }

  spots(q: SpotQuery = {}): Observable<Page<Spot>> {
    let params = new HttpParams();
    Object.entries(q).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') params = params.set(k, String(v));
    });
    return this.http.get<Page<Spot>>(`${this.base}/spots`, { params });
  }

  spot(id: number): Observable<Spot> {
    return this.http.get<Spot>(`${this.base}/spots/${id}`);
  }

  stats(): Observable<SpotStats> {
    return this.http.get<SpotStats>(`${this.base}/spots/stats`);
  }

  createSpot(req: CreateSpotRequest): Observable<Spot> {
    return this.http.post<Spot>(`${this.base}/spots`, req);
  }

  confirm(id: number): Observable<SpotAction> {
    return this.http.post<SpotAction>(`${this.base}/spots/${id}/confirm`, {});
  }

  clean(id: number, afterPhotoUrl: string): Observable<SpotAction> {
    return this.http.post<SpotAction>(`${this.base}/spots/${id}/clean`, { afterPhotoUrl });
  }

  reject(id: number): Observable<SpotAction> {
    return this.http.post<SpotAction>(`${this.base}/spots/${id}/reject`, {});
  }

  me(): Observable<Me> {
    return this.http.get<Me>(`${this.base}/me`);
  }

  entities(type?: EntityType): Observable<Page<EntitySummary>> {
    const params = type ? new HttpParams().set('type', type) : undefined;
    return this.http.get<Page<EntitySummary>>(`${this.base}/entities`, { params });
  }

  dashboard(entityId: number): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.base}/entities/${entityId}/dashboard`);
  }

  citizenLeaderboard(size = 10): Observable<Page<CitizenEntry>> {
    return this.http.get<Page<CitizenEntry>>(`${this.base}/leaderboard/citizens`, { params: { size } });
  }

  entityLeaderboard(type?: EntityType, size = 10): Observable<Page<EntityEntry>> {
    let params = new HttpParams().set('size', size);
    if (type) params = params.set('type', type);
    return this.http.get<Page<EntityEntry>>(`${this.base}/leaderboard/entities`, { params });
  }

  wardScores(): Observable<WardScore[]> {
    return this.http.get<WardScore[]>(`${this.base}/wards/haunted-scores`);
  }

  config(): Observable<{ photoUploadsEnabled: boolean }> {
    return this.http.get<{ photoUploadsEnabled: boolean }>(`${this.base}/config`);
  }

  uploadPhoto(file: File): Observable<{ url: string }> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<{ url: string }>(`${this.base}/photos`, form);
  }

  runOverdueJob(): Observable<{ penalisedSpots: number }> {
    return this.http.post<{ penalisedSpots: number }>(`${this.base}/jobs/overdue-penalties`, {});
  }
}
