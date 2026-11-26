# Requirements: cursed-spots

## Introduction
Clusters of repeated dumping are "cursed": the strongest visual warning on the map, and a penalty for the responsible entity.

### R1 Detection
1. WHEN a spot is created THEN the system SHALL find OPEN spots created in the last 30 days within 50 m of it (Haversine).
2. IF that set, including the new spot, has 3 or more spots THEN the system SHALL mark all of them `cursed = true`.
3. Spots older than 30 days, CLEANED or REJECTED spots, and spots farther than 50 m SHALL NOT count.
4. A cursed spot SHALL stay cursed until it is cleaned or rejected.

### R2 Penalty
1. WHEN a linked spot becomes cursed THEN its entity SHALL lose 50, at most once per spot.

### R3 Display
1. Cursed spots SHALL show a pulsing red aura on the map, a "CURSED" label in lists, and an explanation on the details page.
2. The landing page and map header SHALL show the number of cursed monsters.

### R4 Seed
1. The seed data SHALL include one cursed cluster (4 spots behind Green Meadows Society, Koramangala).
