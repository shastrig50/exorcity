# Design: deployment

```
Browser ──► Netlify/Vercel (static Angular, SPA fallback)
   │  API_URL baked in at build time
   ▼
Render web service (Docker, Spring Boot, $PORT) ──► Render PostgreSQL (DATABASE_URL)
   └─ optional Cloudinary (CLOUDINARY_URL)
```

- `config/DatabaseUrlEnvironmentPostProcessor` (registered in `META-INF/spring.factories`) rewrites `postgres://` URLs into `spring.datasource.*` properties.
- Dockerfile: `maven:3.9-eclipse-temurin-21` → `eclipse-temurin:21-jre-alpine`; JVM flags `-XX:MaxRAMPercentage=75 -XX:+UseSerialGC` for the free plan's 512 MB.
- `frontend/scripts/set-env.mjs` generates `environment.prod.ts`; `build:prod` = set-env + `ng build`.
- Flyway runs on startup; the seeder fills an empty database once.

## Env vars
| Name | Where | Notes |
|---|---|---|
| DATABASE_URL | Render (from DB) | postgres:// or jdbc: |
| JWT_SECRET | Render (generated) | ≥ 32 chars |
| JWT_EXPIRATION_MINUTES | Render | default 720 |
| CORS_ALLOWED_ORIGINS | Render | the frontend URL |
| CLOUDINARY_URL | Render | optional |
| SEED_DEMO_DATA | Render | true for the demo |
| API_URL | Netlify/Vercel | `https://<render-app>.onrender.com/api` |
