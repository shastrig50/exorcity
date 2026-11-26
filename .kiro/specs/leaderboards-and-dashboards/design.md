# Design: leaderboards-and-dashboards

## Backend
- `leaderboard/LeaderboardService`: ranks computed from the page offset; `wardHauntedScores()` groups OPEN spots by ward (wards come from entities, so wards with no spots still appear).
- `entity/EntityService.dashboard(entityId, viewerId)`: access check (own admin, or ward officer of the same ward); reuses `SpotMapper`.
- `seed/DataSeeder` (`CommandLineRunner`, only on an empty `users` table):
  - Wards: Koramangala, Indiranagar, Whitefield, Jayanagar, Hebbal, Malleshwaram.
  - Entities: Green Meadows Society, Lakeview Residency, Silver Oak Apartments, Tech Park Alpha, Orion Business Campus, Nimbus Tech Campus, Corner Kirana Store, Fresh Bites Bakery (all fictional).
  - 40 spots (ages 0–60 days; 32 open, 7 cleaned, 1 rejected); confirmations, cleanings, rejection, curses and overdue penalties are replayed through `CivicScoreService` with realistic timestamps.
  - Asha (citizen@) is never used as a seeded confirmer, so she can confirm live in demos.

## Frontend
- `LeaderboardsComponent`: `forkJoin` of 5 calls; medals for the top 3; ward bars scaled to the most haunted ward.
- `DashboardComponent`: entity admin view (score meter, stats, linked spots, history) or ward officer view (ward stats, open spots, "Run daily overdue job now").
- `LandingComponent`: two fog layers, live stats from `/api/spots/stats`, how-it-works steps, bestiary, level scale.
