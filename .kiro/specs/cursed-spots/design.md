# Design: cursed-spots

- `spot/CurseDetector.cursedCluster(center, candidates, now)` (pure): returns the cluster (including the centre) or an empty list.
- `spot/CurseService.evaluate(spot, now)`:
  1. Bounding-box prefilter in SQL (`findByStatusAndCreatedAtAfterAndLatitudeBetweenAndLongitudeBetween`, ±50 m via `GeoUtils.latDelta/lngDelta`).
  2. Exact Haversine check in Java.
  3. Mark the cluster cursed; for each linked spot where `!cursePenaltyApplied`, apply −50 through `CivicScoreService` and set the flag.
- Cleaning or rejecting sets `cursed = false`.
- The seeder replays the detector over the open seed spots (oldest first), so the seeded cluster and penalties match the live rules.
- CSS: `.monster.cursed::after` is an expanding red ring (`curse-aura` keyframes).

## Testing
- `CurseDetectorTest`: 3 nearby spots → cursed; 2 → not; far, old or cleaned spots are ignored.
- API smoke test: a new report inside the cluster comes back `cursed: true` and the entity is charged −50.
