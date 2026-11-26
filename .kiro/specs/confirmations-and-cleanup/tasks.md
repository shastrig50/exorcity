# Tasks: confirmations-and-cleanup

- [x] 1. `Confirmation` entity + repository (unique per user per spot) — _R1_
- [x] 2. `SpotService.confirm` with +5 / first +10 and 409 rules — _R1_
- [x] 3. `SpotService.clean` with role checks, after photo, +25, fast-clean +20 — _R2_
- [x] 4. `SpotService.reject` (same-ward officer, reporter −30) — _R3_
- [x] 5. Endpoints `POST /api/spots/{id}/confirm|clean|reject` — _R1–R3_
- [x] 6. `SpotServiceTest` (9 cases) — _R1–R3_
- [x] 7. Spot page actions with role-aware visibility and messages — _R4.1_
- [x] 8. Smoke exorcism animation + reduced-motion fallback + points toast — _R4.2, R4.3_
