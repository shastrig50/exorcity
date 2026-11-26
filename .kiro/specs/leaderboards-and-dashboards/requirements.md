# Requirements: leaderboards-and-dashboards

## Introduction
Public leaderboards, entity and ward dashboards, the spooky landing page and the Bengaluru seed data.

### R1 Leaderboards
1. `GET /api/leaderboard/citizens` SHALL list CITIZENs by score (paginated) with rank, rating, reward points, reports and exorcisms.
2. `GET /api/leaderboard/entities?type=` SHALL list entities by score with rank and open-spot count.
3. `GET /api/wards/haunted-scores` SHALL rank wards by `Σ open monster levels + 2 × cursed open spots`.
4. The page SHALL show Top Ghost Hunters, Cleanest Societies, Best Corporate Adopters (plus shops) and Most Haunted Wards.

### R2 Entity dashboard
1. `GET /api/entities/{id}/dashboard` SHALL return the entity, its stats (open, cleaned, cursed, overdue, worst level), linked spots (open first, scariest first) and score history.
2. Only that entity's admins, or the ward officer of its ward, SHALL access it; others get 403.
3. The dashboard SHALL offer a "Clean" action for each open linked spot.

### R3 Ward officer view
1. A WARD_OFFICER's dashboard SHALL list all open spots in their ward with the ward's haunted score and rank, and link to the review (clean/reject) page.

### R4 Landing page
1. The landing page SHALL show a fog-animated hero with the tagline, a live count of open, cursed and exorcised monsters, and a "Start Hunting" button.
2. It SHALL explain the flow (spot, confirm, exorcise) and the monster bestiary and level scale.

### R5 Seed data
1. On an empty database the system SHALL seed 6 Bengaluru wards, 8 fictional entities, demo users for every role (password `Demo@123`) and about 40 spots of mixed ages, types and statuses, including one cursed cluster.
2. Seeding SHALL use the same scoring rules, so the history and scores are consistent. It can be disabled with `SEED_DEMO_DATA=false`.
