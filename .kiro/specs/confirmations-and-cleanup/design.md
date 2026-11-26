# Design: confirmations-and-cleanup

## Backend
- `Confirmation` entity, unique `(spot_id, user_id)` (DB constraint; a race condition returns 409 via the `DataIntegrityViolationException` handler).
- `SpotService.confirm / clean / reject` are each one `@Transactional` unit; all score changes go through `CivicScoreService`.
- `SpotService.canClean(user, spot)` is a static, testable rule.
- Responses use `SpotActionResponse { spot, pointsEarned, newCivicScore }` so the UI can update the header score immediately.
- The bookkeeping flag `firstConfirmationRewarded` guarantees the +10 is paid only once.

## Frontend
- `SpotDetailComponent`: computed `isMine`, `canReject`; `AuthService.canClean(spot)` mirrors the backend rule (the backend still enforces it).
- Exorcism: `.exorcising` class on the stage → `exorcise-monster` keyframes plus 6 `.smoke-puff` elements with staggered delays and a per-puff `--dx`; after 1.3 s the cleaned state replaces the monster.
- `PhotoInputComponent` is reused for the after photo, with an "exorcised" sample image.

## Testing
- `SpotServiceTest` (Mockito): own-confirm, duplicate confirm, first-confirmation rewards, fast vs slow clean, wrong admin, reject by the right ward vs another ward.
- `CivicScoreProperties` / `CivicScoreServiceTest`: cleaning never lowers the score.
