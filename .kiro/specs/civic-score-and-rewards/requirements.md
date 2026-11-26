# Requirements: civic-score-and-rewards

## Introduction
A Civic Score (0–900) for every citizen and entity, with an audit trail, ratings, reward points and a daily penalty job.

### R1 Score engine
1. Every score change SHALL go through one service that clamps the result to [0, 900] and records a ScoreEvent with the delta actually applied.
2. For any sequence of events the score SHALL stay within [0, 900]. *(property test)*
3. Citizen deltas: first confirmation of their report +10, confirming +5, cleaning +25, rejected report −30.
4. Entity deltas: linked spot cleaned within 3 days +20, linked spot open > 7 days −15, linked spot cursed −50.

### R2 Daily job
1. The system SHALL run a scheduled job daily at 02:00 Asia/Kolkata.
2. WHEN it runs THEN every OPEN linked spot older than 7 days that has not been penalised SHALL cost its entity 15, once per spot.
3. A ward officer SHALL be able to trigger the job on demand (`POST /api/jobs/overdue-penalties`) for demos.

### R3 Ratings and rewards
1. Rating: ≥ 750 Ghost Free, 600–749 Calm Spirits, 400–599 Haunted, < 400 Cursed.
2. Reward points = max(0, score − 600) / 10 (integer); display only, no payments.

### R4 Profile
1. `GET /api/me` SHALL return the profile, score, rating, reward points, activity stats and the latest 50 score events.
2. The profile page SHALL show the score as a glowing semicircular meter coloured by rating, a rating badge, reward points and the history list.
