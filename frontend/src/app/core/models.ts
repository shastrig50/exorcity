export type Role = 'CITIZEN' | 'SOCIETY_ADMIN' | 'CORPORATE_ADMIN' | 'SHOP_OWNER' | 'WARD_OFFICER';
export type GarbageType = 'ROADSIDE_PILE' | 'OVERFLOWING_BIN' | 'PLASTIC_WASTE' | 'ILLEGAL_DUMP' | 'LAKE_OR_DRAIN';
export type SpotStatus = 'OPEN' | 'CLEANED' | 'REJECTED';
export type EntityType = 'SOCIETY' | 'CORPORATE_CAMPUS' | 'SHOP';
export type Rating = 'GHOST_FREE' | 'CALM_SPIRITS' | 'HAUNTED' | 'CURSED';

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface UserSummary {
  id: number;
  name: string;
  email: string;
  role: Role;
  civicScore: number;
  entityId: number | null;
  ward: string | null;
}

export interface AuthResponse {
  token: string;
  expiresAt: string;
  user: UserSummary;
}

export interface Spot {
  id: number;
  type: GarbageType;
  monsterName: string;
  monsterEmoji: string;
  description: string | null;
  latitude: number;
  longitude: number;
  beforePhotoUrl: string;
  afterPhotoUrl: string | null;
  status: SpotStatus;
  reportedById: number;
  reportedByName: string | null;
  cleanedById: number | null;
  cleanedByName: string | null;
  linkedEntityId: number | null;
  linkedEntityName: string | null;
  ward: string | null;
  createdAt: string;
  cleanedAt: string | null;
  ageDays: number;
  monsterLevel: number;
  confirmationCount: number;
  cursed: boolean;
  confirmedByMe: boolean;
}

export interface SpotAction {
  spot: Spot;
  pointsEarned: number;
  newCivicScore: number;
}

export interface SpotStats {
  openMonsters: number;
  cursedMonsters: number;
  exorcised: number;
}

export interface CreateSpotRequest {
  type: GarbageType;
  description: string;
  latitude: number;
  longitude: number;
  beforePhotoUrl: string;
  ward?: string | null;
}

export interface ScoreEvent {
  id: number;
  points: number;
  reason: string;
  description: string;
  spotId: number | null;
  createdAt: string;
}

export interface Me {
  id: number;
  name: string;
  email: string;
  role: Role;
  civicScore: number;
  rating: Rating;
  ratingLabel: string;
  rewardPoints: number;
  entityId: number | null;
  entityName: string | null;
  ward: string | null;
  stats: { reports: number; confirmations: number; exorcisms: number };
  history: ScoreEvent[];
}

export interface EntitySummary {
  id: number;
  name: string;
  type: EntityType;
  latitude: number;
  longitude: number;
  ward: string;
  civicScore: number;
  rating: Rating;
  ratingLabel: string;
  rewardPoints: number;
  openSpots: number;
}

export interface Dashboard {
  entity: EntitySummary;
  stats: { openSpots: number; cleanedSpots: number; cursedSpots: number; overdueSpots: number; worstMonsterLevel: number };
  spots: Spot[];
  history: ScoreEvent[];
}

export interface CitizenEntry {
  rank: number;
  id: number;
  name: string;
  civicScore: number;
  rating: Rating;
  ratingLabel: string;
  rewardPoints: number;
  reports: number;
  exorcisms: number;
}

export interface EntityEntry {
  rank: number;
  id: number;
  name: string;
  type: EntityType;
  ward: string;
  civicScore: number;
  rating: Rating;
  ratingLabel: string;
  openSpots: number;
}

export interface WardScore {
  rank: number;
  ward: string;
  openSpots: number;
  cursedSpots: number;
  worstMonsterLevel: number;
  hauntedScore: number;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  fieldErrors?: Record<string, string> | null;
}
