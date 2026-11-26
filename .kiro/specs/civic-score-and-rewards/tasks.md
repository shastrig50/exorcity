# Tasks: civic-score-and-rewards

- [x] 1. `CivicScorePolicy`, `Rating`, `ScoreReason` — _R1, R3_
- [x] 2. `ScoreEvent` + repository; `CivicScoreService` as the single entry point — _R1.1_
- [x] 3. Wire citizen/entity deltas into confirm/clean/reject/curse — _R1.3, R1.4_
- [x] 4. `OverdueSpotJob` (daily cron, once per spot) + `POST /api/jobs/overdue-penalties` — _R2_
- [x] 5. `ProfileService` + `GET /api/me` — _R4.1_
- [x] 6. Property tests: bounds, cleaning never lowers the score; service tests — _R1.2_
- [x] 7. Profile page: score meter, rating badge, reward points, history — _R4.2_
