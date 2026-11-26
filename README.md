# 👻 Exorcity

> **Your city is haunted by garbage. Exorcise it, and get rewarded.**

Exorcity is a garbage-reporting and cleanliness-rating web app for Indian metro cities, starting with Bengaluru.
Every garbage spot appears on a dark city map as a **monster**. The longer it stays uncleaned, the bigger and scarier it gets.
Clean it with an "after" photo and the monster vanishes in a puff of smoke. Citizens and responsible entities (housing
societies, corporate campuses, shops) earn a **Civic Score (0–900)** with ratings and reward points.

Built for **Kiroween 2026, Costume Contest**. The spooky UI carries meaning: *a scarier monster means a more urgent problem*.

## Monsters
| Garbage type | Monster |
|---|---|
| Roadside pile | 🧟 Trash Zombie |
| Overflowing bin | 👹 Bin Beast |
| Plastic waste | 👻 Plastic Phantom |
| Illegal dump | 🕷️ Dump Demon |
| Lake or drain | 🐸 Swamp Ghoul |

- **Level 1–5** grows with age (0–2, 3–7, 8–14, 15–30, 30+ days) and with confirmations (+1 per 5). Bigger, redder and faster-pulsing means more urgent.
- **Cursed**: 3+ open spots within 50 m in 30 days get a pulsing red aura, and the linked entity loses 50.
- **Responsibility**: each spot is linked to the nearest society, campus or shop within 200 m (Haversine); otherwise the ward is responsible.

## Civic Score
| Event | Points |
|---|---|
| Your report gets its first confirmation | +10 |
| You confirm someone else's spot | +5 |
| You clean a spot (after photo) | +25 |
| Your report is rejected as fake | −30 |
| Entity: linked spot cleaned within 3 days | +20 |
| Entity: linked spot open > 7 days (daily job, once per spot) | −15 |
| Entity: linked spot becomes cursed | −50 |

Scores are clamped to 0–900, and every change is recorded in an audit trail.
Ratings: **750+ Ghost Free · 600–749 Calm Spirits · 400–599 Haunted · <400 Cursed**. Reward points = (score − 600) / 10.

## Tech stack
- **Backend:** Java 21, Spring Boot 3.5, Spring Security (JWT via the built-in OAuth2 resource server), Spring Data JPA, PostgreSQL, Flyway, springdoc-openapi
- **Frontend:** Angular 19 (standalone components, signals), Tailwind CSS 3, Leaflet + OpenStreetMap (no API key)
- **Tests:** JUnit 5, Mockito, jqwik (property-based)

## Repository layout
```
backend/     Spring Boot API (controller → service → repository, DTO records)
frontend/    Angular app (core/ services, pages/, shared/ UI)
.kiro/       Kiro steering, specs and hooks
docker-compose.yml   local PostgreSQL
render.yaml          Render blueprint
```

## Run locally
Prerequisites: JDK 21+, Maven 3.9+, Node 20+, Docker.

```bash
# 1. Database (host port 55432, so it won't clash with a local Postgres)
docker compose up -d

# 2. Backend -> http://localhost:8080   (Swagger UI: http://localhost:8080/swagger-ui.html)
cd backend
mvn spring-boot:run          # applies Flyway migrations and seeds Bengaluru demo data on first run

# 3. Frontend -> http://localhost:4200
cd frontend
npm install
npm start
```

To reset the demo data, run `docker compose down -v && docker compose up -d` and restart the backend.

### Configuration
All configuration uses environment variables. See [`.env.example`](.env.example).

| Variable | Default (local) | Purpose |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:55432/exorcity` | JDBC URL, or a `postgres://user:pass@host/db` URL (Render) |
| `DB_USERNAME` / `DB_PASSWORD` | `exorcity` / `exorcity` | DB credentials |
| `JWT_SECRET` | dev-only value | **Must** be set in production (32+ chars) |
| `JWT_EXPIRATION_MINUTES` | `720` | Token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Comma-separated frontend origins |
| `CLOUDINARY_URL` | _(empty)_ | Optional `cloudinary://key:secret@cloud`. Enables photo upload; otherwise image URLs are accepted |
| `SEED_DEMO_DATA` | `true` | Seed Bengaluru demo data into an empty DB |
| `PORT` | `8080` | HTTP port |
| `DB_PORT` (compose only) | `55432` | Host port for the Docker Postgres |
| `API_URL` (frontend build) | `/api` | Backend base URL baked into the production build |

## Tests
```bash
cd backend && mvn verify        # 31 tests: unit + Mockito + jqwik properties
cd frontend && npm run build    # production build
```
Property-based tests (jqwik):
- Civic Score always stays within 0–900 for any sequence of events
- Monster level is always 1–5 and never decreases as age (or confirmations) increase
- Cleaning a spot never lowers the cleaner's score
- Entity linking never links a spot to an entity more than 200 m away

## Demo users
All demo users use the password **`Demo@123`**. The login page has a quick-login button for each role.

| Role | Email | Notes |
|---|---|---|
| Citizen (Ghost Hunter) | `citizen@exorcity.demo` | Asha Rao: report, confirm, clean |
| Citizen | `hunter@exorcity.demo` | Ravi Kumar (plus 8 more citizens for the leaderboard) |
| Society admin | `society@exorcity.demo` | Green Meadows Society, Koramangala (has the cursed cluster) |
| Society admin | `lakeview@exorcity.demo` | Lakeview Residency, Hebbal |
| Corporate admin | `corporate@exorcity.demo` | Tech Park Alpha, Whitefield |
| Shop owner | `shop@exorcity.demo` | Corner Kirana Store, Malleshwaram |
| Ward officer | `ward@exorcity.demo` | Koramangala ward: clean or reject reports, run the daily job |

Seed data: 6 wards (Koramangala, Indiranagar, Whitefield, Jayanagar, Hebbal, Malleshwaram), 8 fictional entities and
40 spots (32 open across all five levels, 7 cleaned, 1 rejected), including a 4-spot cursed cluster. All names are fictional.

## API
Base path: `/api`. Interactive docs: `/swagger-ui.html`. List endpoints accept `page` and `size` (max 100).

<!-- API:START -->
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | /api/auth/login | Public | Log in and receive a JWT |
| POST | /api/auth/register | Public | Register a new citizen ghost hunter |
| GET | /api/config | Public | Client feature flags (photo upload enabled?) |
| GET | /api/entities | Public | List entities by civic score (`?type=`) |
| GET | /api/entities/{id}/dashboard | Entity admin / ward officer | Linked spots, stats and score history |
| GET | /api/health | Public | Liveness check |
| POST | /api/jobs/overdue-penalties | WARD_OFFICER | Run the daily overdue job now (demo helper) |
| GET | /api/leaderboard/citizens | Public | Top ghost hunters |
| GET | /api/leaderboard/entities | Public | Cleanest entities (`?type=SOCIETY\|CORPORATE_CAMPUS\|SHOP`) |
| GET | /api/me | Logged in | Profile, civic score, rating, reward points, score history |
| POST | /api/photos | Logged in | Upload a photo to Cloudinary (if configured) |
| GET | /api/spots | Public | List spots (`?status=&ward=&bbox=minLng,minLat,maxLng,maxLat`) |
| POST | /api/spots | CITIZEN | Report a spot (auto-links the nearest entity within 200 m) |
| GET | /api/spots/stats | Public | Live open / cursed / exorcised counts |
| GET | /api/spots/{id} | Public | Spot details with monster level |
| POST | /api/spots/{id}/clean | Citizen / own entity admin / ward officer | Exorcise with an after photo (+25) |
| POST | /api/spots/{id}/confirm | CITIZEN | Confirm someone else's sighting (+5, reporter +10 on first) |
| POST | /api/spots/{id}/reject | WARD_OFFICER (same ward) | Reject a fake report (reporter −30) |
| GET | /api/wards/haunted-scores | Public | Most haunted wards |
<!-- API:END -->

## Deployment
### Backend → Render
1. Push the repo to GitHub.
2. Render dashboard → **New → Blueprint** → select the repo. `render.yaml` creates a free PostgreSQL database and the `exorcity-api` Docker service (`backend/Dockerfile`).
3. Set `CORS_ALLOWED_ORIGINS` to your frontend URL (e.g. `https://exorcity.netlify.app`). Optionally set `CLOUDINARY_URL`.
4. Wait for `/api/health` to return `UP`. The first boot migrates and seeds the database.

### Frontend → Netlify (or Vercel)
- **Netlify:** New site from Git → Base directory `frontend` (uses `frontend/netlify.toml`). Add env var `API_URL=https://<your-render-app>.onrender.com/api`.
- **Vercel:** Import the repo → Root directory `frontend` (uses `frontend/vercel.json`). Add the same `API_URL` env var.

Both run `npm run build:prod`, publish `dist/frontend/browser` and rewrite all routes to `index.html`.

> Free Render services sleep when idle; open `/api/health` a minute before a demo to wake the backend.

## Accessibility
WCAG AA colour contrast, visible focus rings, a skip link, aria-labels on every monster marker, alt text on all photos,
a sortable list view as a keyboard and screen-reader alternative to the map, and a **Motion on/off** toggle that also
respects `prefers-reduced-motion` (fog, pulse, shake and smoke are disabled).

## Screenshots
_Add screenshots to `docs/screenshots/` and link them here:_
Landing · Haunted Map · Report · Spot Details (before/after) · Exorcism · Profile · Entity Dashboard · Leaderboards

## Built with Kiro
The `.kiro/` folder contains:
- **Steering:** `product.md`, `tech.md`, `design.md`, `domain.md`
- **Specs:** 9 features, each with `requirements.md` (EARS), `design.md` and `tasks.md`
- **Hooks:** run related tests on Java save; regenerate this README's API table when a controller changes; accessibility check on new Angular components

## License
[MIT](LICENSE)
