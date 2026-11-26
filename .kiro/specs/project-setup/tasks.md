# Tasks: project-setup

- [x] 1. Repo root: `git init`, `.gitignore` (keep `.kiro/`), MIT `LICENSE`, `.env.example`, `docker-compose.yml` (Postgres 16 + healthcheck) — _R4, R5_
- [x] 2. Backend Maven project (`pom.xml`, Java 21 release, Spring Boot 3.5) and `ExorcityApplication` — _R1.1_
- [x] 3. `application.yml` with env var mapping; `ClockConfig`; temporary `SecurityConfig` (stateless, health and swagger public) — _R1.5_
- [x] 4. Flyway `V1__init.sql` with all tables, constraints and indexes — _R2_
- [x] 5. `HealthController` + `GlobalExceptionHandler` + `ApiError`; `HealthControllerTest` (WebMvcTest) — _R1.3_
- [x] 6. Angular app (standalone, routing, no SSR) + Tailwind 3.4 with the design tokens and fonts — _R3.1–R3.3_
- [x] 7. App shell: skip link, responsive nav, reduced-motion toggle (`MotionService`), placeholder home page — _R3.4_
- [x] 8. Environment files with `apiUrl` — _R3.5_
- [x] 9. Kiro hooks (3) — _R6_
- [x] 10. README (setup, run, API markers, demo users placeholder, deploy, screenshots) — _R5.1_
- [x] 11. Verify: `mvn verify` and `npm run build` both pass
