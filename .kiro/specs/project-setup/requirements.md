# Requirements: project-setup

## Introduction
Set up the monorepo skeleton for Exorcity: a buildable Spring Boot backend, an Angular + Tailwind frontend, a local PostgreSQL via docker-compose, the full database schema via Flyway, licensing, docs and Kiro hooks. All later specs build on this.

## Requirements

### R1 Backend skeleton
**User story:** As a developer, I want a runnable Spring Boot app so that features can be added in layers.
1. WHEN `mvn verify` is run in `/backend` THEN the system SHALL compile for Java 21 and run all tests successfully.
2. WHEN the backend starts with a reachable PostgreSQL THEN the system SHALL apply Flyway migrations before serving requests.
3. WHEN a client calls `GET /api/health` THEN the system SHALL return `200` with `{ "status": "UP", "app": "exorcity" }`.
4. WHEN a client opens `/swagger-ui.html` THEN the system SHALL show the OpenAPI documentation.
5. The system SHALL read every connection setting and secret from environment variables with local-only defaults.

### R2 Database schema
1. The system SHALL create the tables `users`, `responsible_entities`, `garbage_spots`, `confirmations` and `score_events` in migration `V1`.
2. The system SHALL enforce uniqueness on `users.email` and on `confirmations (spot_id, user_id)`.
3. The system SHALL enforce `civic_score BETWEEN 0 AND 900` with check constraints.
4. The system SHALL index `garbage_spots` on `status`, `ward` and `(latitude, longitude)`.

### R3 Frontend skeleton
1. WHEN `npm run build` is run in `/frontend` THEN the system SHALL produce a production bundle without errors.
2. The system SHALL use standalone components only, with Tailwind configured using the design tokens from `design.md`.
3. The system SHALL load the Creepster and Inter fonts, set the `night` background and use `bone` text globally.
4. The system SHALL provide a shell with a skip link, a responsive nav with visible focus states, and a reduced-motion toggle that respects `prefers-reduced-motion`.
5. The system SHALL read the API base URL from the environment files.

### R4 Local environment
1. WHEN `docker compose up -d` is run THEN the system SHALL start PostgreSQL 16 with database `exorcity` on host port 55432 (configurable via `DB_PORT`, avoids clashing with a native Postgres) and a health check.

### R5 Repository hygiene
1. The repository SHALL contain an MIT `LICENSE`, a `README.md` (setup, run, deploy, demo users, API, screenshots) and a `.gitignore` that does **not** ignore `.kiro/`.
2. The repository SHALL contain `.env.example` and no real secrets.

### R6 Kiro hooks
1. WHEN a Java file under `backend/src` is saved THEN the agent SHALL run the related unit tests.
2. WHEN a `*Controller.java` file changes THEN the agent SHALL update the API section of the README.
3. WHEN a new Angular component file is created THEN the agent SHALL check accessibility (aria labels, contrast, alt text).
