import { EntityType, GarbageType, Rating, Role, Spot } from './models';

export interface MonsterInfo {
  type: GarbageType;
  name: string;
  emoji: string;
  label: string;
}

export const MONSTERS: MonsterInfo[] = [
  { type: 'ROADSIDE_PILE', name: 'Trash Zombie', emoji: '🧟', label: 'Roadside pile' },
  { type: 'OVERFLOWING_BIN', name: 'Bin Beast', emoji: '👹', label: 'Overflowing bin' },
  { type: 'PLASTIC_WASTE', name: 'Plastic Phantom', emoji: '👻', label: 'Plastic waste' },
  { type: 'ILLEGAL_DUMP', name: 'Dump Demon', emoji: '🕷️', label: 'Illegal dump' },
  { type: 'LAKE_OR_DRAIN', name: 'Swamp Ghoul', emoji: '🐸', label: 'Lake or drain' },
];

export function monsterFor(type: GarbageType): MonsterInfo {
  return MONSTERS.find((m) => m.type === type) ?? MONSTERS[0];
}

/** Urgency language for each monster level (design.md). */
export const LEVELS = [
  { level: 1, size: 28, title: 'New', range: '0–2 days' },
  { level: 2, size: 34, title: 'Lingering', range: '3–7 days' },
  { level: 3, size: 42, title: 'Festering', range: '8–14 days' },
  { level: 4, size: 50, title: 'Rotting', range: '15–30 days' },
  { level: 5, size: 60, title: 'Ancient horror', range: '30+ days' },
];

export function levelInfo(level: number) {
  return LEVELS[Math.min(Math.max(level, 1), 5) - 1];
}

export function spotAriaLabel(s: Spot): string {
  const age = s.ageDays === 1 ? '1 day' : `${s.ageDays} days`;
  if (s.status !== 'OPEN') return `${s.monsterName} spot, ${s.status.toLowerCase()}`;
  return `${s.monsterName}, level ${s.monsterLevel} ${levelInfo(s.monsterLevel).title.toLowerCase()}, open ${age}` +
    `${s.cursed ? ', cursed' : ''}${s.linkedEntityName ? `, near ${s.linkedEntityName}` : ''}`;
}

export const RATING_STYLES: Record<Rating, { text: string; ring: string; color: string }> = {
  GHOST_FREE: { text: 'text-ecto', ring: 'border-ecto/60', color: '#39ff14' },
  CALM_SPIRITS: { text: 'text-pumpkin', ring: 'border-pumpkin/60', color: '#ff6b00' },
  HAUNTED: { text: 'text-[#ff8a5c]', ring: 'border-[#ff8a5c]/60', color: '#ff8a5c' },
  CURSED: { text: 'text-[#ff5a5a]', ring: 'border-[#ff5a5a]/60', color: '#ff3b3b' },
};

export const ROLE_LABELS: Record<Role, string> = {
  CITIZEN: 'Ghost Hunter',
  SOCIETY_ADMIN: 'Society Admin',
  CORPORATE_ADMIN: 'Corporate Admin',
  SHOP_OWNER: 'Shop Owner',
  WARD_OFFICER: 'Ward Officer',
};

export const ENTITY_TYPE_LABELS: Record<EntityType, string> = {
  SOCIETY: 'Housing society',
  CORPORATE_CAMPUS: 'Corporate campus',
  SHOP: 'Shop',
};

export function ratingFor(score: number): Rating {
  if (score >= 750) return 'GHOST_FREE';
  if (score >= 600) return 'CALM_SPIRITS';
  if (score >= 400) return 'HAUNTED';
  return 'CURSED';
}
