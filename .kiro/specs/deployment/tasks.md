# Tasks: deployment

- [x] 1. `backend/Dockerfile` (multi-stage, non-root, `$PORT`) + `.dockerignore` — _R1.1, R1.2_
- [x] 2. `DatabaseUrlEnvironmentPostProcessor` for Render-style URLs — _R1.3_
- [x] 3. `render.yaml` blueprint (DB + web service + env vars) — _R2_
- [x] 4. `scripts/set-env.mjs` + `build:prod` script — _R3.1_
- [x] 5. `netlify.toml` and `vercel.json` with SPA fallback — _R3.2_
- [x] 6. CORS from env; `.env.example` — _R4_
- [ ] 7. Deploy to Render + Netlify/Vercel and smoke-test the live URLs (manual, see the README checklist)
