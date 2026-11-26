# Deploy Exorcity in 15 minutes (free)

**Stack:** Supabase (PostgreSQL) → Render (Spring Boot) → Netlify (Angular)  
**Cost:** $0 · No credit card required

---

## Demo credentials (all roles)

| Role | Email | Password |
|---|---|---|
| Citizen | citizen@exorcity.demo | Demo@123 |
| Top hunter | hunter@exorcity.demo | Demo@123 |
| Society admin | society@exorcity.demo | Demo@123 |
| Corporate admin | corporate@exorcity.demo | Demo@123 |
| Shop owner | shop@exorcity.demo | Demo@123 |
| Ward officer | ward@exorcity.demo | Demo@123 |

---

## Step 1 — Database (Supabase, ~3 min)

1. Go to **https://supabase.com** → Sign up → New project
2. Name: `exorcity` · Password: choose a strong one · Region: **Singapore**
3. Wait ~1 min for provisioning
4. Go to **Settings → Database → Connection string → URI** tab
5. Copy the URI — looks like:  
   `postgresql://postgres:[YOUR-PASSWORD]@db.xxxx.supabase.co:5432/postgres`
6. Keep this tab open — you need it in Step 2

> Free tier: 500 MB, never expires, pauses after 7 days inactivity (auto-wakes on next request).

---

## Step 2 — Backend (Render, ~5 min)

1. Go to **https://render.com** → Sign up with GitHub
2. New → **Web Service** → Connect repo `shastrig50/exorcity`
3. Settings:
   - **Name:** `exorcity`
   - **Root directory:** `backend`
   - **Runtime:** Docker
   - **Plan:** Free
4. Under **Environment Variables**, add these:

| Key | Value |
|---|---|
| `DATABASE_URL` | Supabase URI from Step 1 |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | Your Supabase password |
| `JWT_SECRET` | Any 40+ char random string (e.g. `exorcity-kiroween-2026-jwt-secret-key-abc123`) |
| `JWT_EXPIRATION_MINUTES` | `720` |
| `CORS_ALLOWED_ORIGINS` | `*` ← set properly after Step 3 |
| `PORT` | `8080` |
| `SEED_DEMO_DATA` | `true` |

5. Click **Create Web Service** — first build takes ~4 min
6. Your backend URL will be: `https://exorcity.onrender.com` (or similar)
7. Test it: `https://exorcity.onrender.com/api/health` → should return `{"status":"UP"}`

> Free tier: 750 hrs/month (enough for 24/7). Spins down after 15 min idle; first request after sleep takes ~30s.

---

## Step 3 — Frontend (Netlify, ~3 min)

1. Go to **https://netlify.com** → Sign up with GitHub
2. **Add new site → Import an existing project → GitHub**
3. Select `shastrig50/exorcity`
4. Build settings (Netlify reads `frontend/netlify.toml` automatically, but verify):
   - **Base directory:** `frontend`
   - **Build command:** *(from netlify.toml — leave as-is)*
   - **Publish directory:** `dist/frontend/browser`
5. Under **Environment variables**, add:

| Key | Value |
|---|---|
| `API_URL` | `https://exorcity.onrender.com/api` ← use your actual Render URL |

6. Click **Deploy site** — takes ~1 min
7. Your frontend URL will be: `https://exorcity.netlify.app` (or similar)

---

## Step 4 — Fix CORS (1 min)

1. Go back to Render → Your service → **Environment**
2. Update `CORS_ALLOWED_ORIGINS` to your Netlify URL, e.g.:  
   `https://exorcity.netlify.app`
3. Render auto-redeploys — wait ~30s

---

## Step 5 — Keep the backend awake (optional but recommended)

Render's free tier sleeps after 15 min of no traffic. To avoid the cold-start for demos:

1. Go to **https://cron-job.org** → Sign up (free) → Create cronjob
2. URL: `https://exorcity.onrender.com/api/health`
3. Schedule: every 10 minutes
4. Save — the backend stays warm

---

## Done ✅

| What | URL |
|---|---|
| Frontend | https://exorcity.netlify.app |
| Backend API | https://exorcity.onrender.com/api |
| Swagger UI | https://exorcity.onrender.com/swagger-ui.html |
| Demo password | `Demo@123` |
