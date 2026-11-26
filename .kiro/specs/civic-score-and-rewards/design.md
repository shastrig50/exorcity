# Design: civic-score-and-rewards

- `score/CivicScorePolicy` (pure): `clamp`, `apply(current, delta)` (overflow-safe with long), `rating`, `rewardPoints`.
- `score/ScoreReason` enum holds the nominal points and a human-readable label for each rule.
- `score/CivicScoreService.applyToUser / applyToEntity(…, reason, spotId[, at])` saves the owner and a `ScoreEvent` (exactly one of userId/entityId, enforced by a DB CHECK).
- `score/OverdueSpotJob`: `@Scheduled(cron = "${exorcity.jobs.overdue-cron:0 0 2 * * *}", zone = "Asia/Kolkata")`, calls `run()`, which uses `overduePenaltyApplied` so each spot is penalised once (see the steering decision in domain.md). `SchedulingConfig` can be disabled via `exorcity.jobs.enabled`.
- `user/ProfileService.me` → `MeResponse`; `MeController` stays thin.
- Frontend: `ScoreMeterComponent` (SVG arc with `pathLength=100`, rating-band ticks, `role="meter"`), `RatingBadgeComponent`, `ProfileComponent`.

## Testing
- `CivicScoreProperties`: bounds for any `ScoreReason` sequence and for arbitrary deltas; cleaning never lowers the score; reward points are in 0..30; rating bands.
- `CivicScoreServiceTest`: the recorded event uses the clamped delta; the entity score floors at 0; a jqwik property that cleaning through the service never lowers the score.
