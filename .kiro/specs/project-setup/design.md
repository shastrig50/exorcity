# Design: project-setup

## Backend
- Maven, Spring Boot 3.5.x, `maven.compiler.release=21`, group `com.exorcity`, artifact `exorcity-backend`.
- Starters: web, data-jpa, security, validation; plus flyway-core + flyway-database-postgresql, postgresql, lombok, springdoc-openapi-starter-webmvc-ui, jqwik (test).
- `application.yml` maps env vars:
  ```
  DATABASE_URL (default jdbc:postgresql://localhost:55432/exorcity)
  DB_USERNAME / DB_PASSWORD (default exorcity / exorcity)
  PORT (8080), CORS_ALLOWED_ORIGINS (http://localhost:4200)
  JWT_SECRET, JWT_EXPIRATION_MINUTES (used in auth spec)
  ```
- `jpa.hibernate.ddl-auto=validate`, `open-in-view=false`, timezone UTC.
- A temporary `SecurityConfig` allows everything except as set up in auth spec. In this spec, `/api/health`, swagger and `/v3/api-docs` are public and the API is stateless. CSRF is disabled, which is fine because the API uses bearer tokens.
- `HealthController` → `GET /api/health`.
- `common/GlobalExceptionHandler` + `ApiError` record (used by all later specs).
- `config/ClockConfig` exposes a `Clock.systemUTC()` bean.

## Schema (V1__init.sql)
```
users(id bigserial pk, name, email unique, password_hash, role, civic_score int default 600 check 0..900,
      entity_id fk null, ward null, created_at)
responsible_entities(id, name, type, latitude double, longitude double, ward, civic_score default 600 check 0..900)
garbage_spots(id, type, description, latitude, longitude, before_photo_url, after_photo_url,
      status default 'OPEN', reported_by fk users, cleaned_by fk users null, linked_entity_id fk null,
      ward, created_at, cleaned_at, confirmation_count int default 0, cursed bool default false,
      first_confirmation_rewarded, curse_penalty_applied, overdue_penalty_applied bool default false)
confirmations(id, spot_id fk, user_id fk, created_at, unique(spot_id,user_id))
score_events(id, user_id null, entity_id null, spot_id null, points int, reason, description, created_at,
      check exactly one of user_id/entity_id)
```
Enums are stored as `varchar` (`@Enumerated(STRING)`).

## Frontend
- Angular CLI app in `/frontend`, standalone, routing on, SSR off.
- Tailwind 3.4 (`tailwind.config.js`) with tokens: `night, crypt, pumpkin, blood, ecto, bone, mist`, `fontFamily.creep` = Creepster, `fontFamily.sans` = Inter.
- `styles.css`: Tailwind layers, global focus ring, a `.reduce-motion` class + `@media (prefers-reduced-motion)` that disables animations.
- `core/motion.service.ts`: a signal that holds the reduced-motion preference (localStorage + media query) and toggles the `<html>` class.
- `AppComponent` shell: skip link, header with logo (Creepster) and nav, a motion toggle button with `aria-pressed`, and `<main id="main">`.
- `environments/environment.ts` (`apiUrl: 'http://localhost:8080/api'`) and `environment.prod.ts` (replaced at deploy).

## Hooks (`.kiro/hooks/*.kiro.hook`, JSON)
| File | Trigger | Action |
|---|---|---|
| `run-related-tests.kiro.hook` | fileEdited `backend/src/**/*.java` | askAgent: find `<Name>Test` / `<Name>Properties` and run `mvn -q test -Dtest=...` |
| `update-readme-api.kiro.hook` | fileEdited `backend/src/**/*Controller.java` | askAgent: regenerate the README `## API` table between markers |
| `a11y-check-component.kiro.hook` | fileCreated `frontend/src/app/**/*.component.{ts,html}` | askAgent: check aria labels, alt text, contrast, focus |

## Docs
- README with `<!-- API:START -->` / `<!-- API:END -->` markers so the hook can rewrite the API section.
