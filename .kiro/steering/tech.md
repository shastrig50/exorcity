---
inclusion: always
---

# Tech Stack & Conventions

## Approved stack (ask before adding anything else)
**Backend:** Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security + JWT, Bean Validation, PostgreSQL, Flyway, Lombok, springdoc-openapi (Swagger UI), Maven.
**Frontend:** Angular 17+ standalone components, Tailwind CSS, Leaflet + OpenStreetMap tiles (no API key), Angular signals or simple services for state.
**Photos:** Cloudinary upload when the `CLOUDINARY_URL` env var is set; otherwise accept an image URL string.
**Geo:** Haversine formula in Java (no PostGIS).
**Tests:** JUnit 5, Mockito, jqwik (property-based tests), Spring Boot Test.
**Local dev:** docker-compose with PostgreSQL.
**Deploy:** backend Dockerfile on Render; frontend on Vercel or Netlify.

## Repository structure
```
/backend            Spring Boot app (Maven)
  src/main/java/com/exorcity/
    config/         security, CORS, OpenAPI, scheduling config
    auth/           login/register, JWT filter + service
    user/           User entity, repo, /me
    entity/         Entity (society/campus/shop), dashboard
    spot/           GarbageSpot, Confirmation, reporting, cleaning, cursing
    score/          ScoreEvent, CivicScoreService, daily job
    leaderboard/    leaderboard + ward haunted scores
    common/         exceptions, GlobalExceptionHandler, GeoUtils, paging DTOs
  src/main/resources/db/migration/   Flyway V1__..., V2__seed...
/frontend           Angular app
  src/app/core/     services, interceptors, guards, models
  src/app/pages/    one folder per page (standalone components)
  src/app/shared/   reusable UI (monster-icon, candle-loader, score-meter, ...)
/.kiro              steering, specs, hooks (committed, never gitignored)
docker-compose.yml, README.md, LICENSE (MIT)
```

## Backend conventions
- Layered: **Controller -> Service -> Repository**. Controllers stay thin: they validate, call one service method and map to a DTO.
- Package by feature (see above), not by layer.
- Never expose JPA entities from controllers. Use Java `record` DTOs (`XxxRequest`, `XxxResponse`).
- Validate input with Bean Validation (`@Valid`, `@NotNull`, `@DecimalMin`, ...).
- One `@RestControllerAdvice` GlobalExceptionHandler returns `{ "status", "error", "message", "path", "timestamp" }`.
- REST naming: plural nouns, all under `/api`, actions as sub-resources (`POST /api/spots/{id}/confirm`).
- List endpoints are paginated with Spring `Pageable` (`page`, `size`, default 20, max 100).
- Business rules live in small pure classes (`MonsterLevelCalculator`, `CivicScorePolicy`, `GeoUtils`) so jqwik can test them without Spring.
- Every civic score change goes through `CivicScoreService`, which clamps the score and writes a `ScoreEvent`. No direct `setCivicScore` anywhere else.
- Times use `Instant` (UTC) in storage. Inject a `Clock` bean so tests can control time.
- Schema changes only happen through Flyway migrations. Use `ddl-auto=validate`.

## Frontend conventions
- Standalone components only, no NgModules. Use the new control flow (`@if`, `@for`).
- State lives in Angular signals inside `providedIn: 'root'` services. No NgRx.
- HTTP goes through typed services in `core/`. A JWT interceptor adds the `Authorization` header, and route guards check roles.
- Tailwind for styling. Design tokens are defined in `tailwind.config.js` (see `design.md`).
- The API base URL comes from `environment.ts` / `environment.prod.ts`, so it can be replaced at build time.

## Config & secrets
- **No hardcoded secrets.** All config comes from env vars with safe local defaults:
  `DATABASE_URL`/`DB_USERNAME`/`DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`, `CORS_ALLOWED_ORIGINS`, `CLOUDINARY_URL`, `PORT`.
- `.env` files are gitignored. Commit a `.env.example` instead.

## Quality gates (after every spec)
- `cd backend && mvn verify` passes (unit + jqwik + slice tests).
- `cd frontend && npm run build` passes.
