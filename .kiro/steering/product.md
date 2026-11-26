---
inclusion: always
---

# Product: Exorcity

**Tagline:** "Your city is haunted by garbage. Exorcise it, and get rewarded."

**Hackathon:** Kiroween 2026, Costume Contest category. The spooky, polished UI has to make the app work better, not just decorate it.

## Problem
Garbage gets dumped on roadsides, footpaths, lakes, drains, and near housing societies, office parks, shops and metro stations in Indian metro cities. Nobody clearly owns each spot, so it stays there. Exorcity makes every spot visible, shows who is responsible for it, and makes it urgent.

## Core idea
- Citizens report garbage with a photo and a location.
- Every open spot shows up on a dark city map as a **monster**. The older it gets and the more people confirm it, the bigger and scarier the monster becomes. Scarier means more urgent.
- Cleaning a spot (with an "after" photo) exorcises the monster with a smoke animation.
- Each spot is linked to the nearest responsible **entity** (housing society, corporate campus, shop) within 200 m. If there is none, the ward is responsible.
- Citizens and entities earn a **Civic Score** (0–900) with a spooky rating and redeemable reward points.

## Launch city
Bengaluru: 6 seeded wards, 8 entities and about 40 spots.

## Users and roles
| Role | Can do |
|---|---|
| CITIZEN | Report spots, confirm other people's spots, clean spots |
| SOCIETY_ADMIN / CORPORATE_ADMIN / SHOP_OWNER | View the dashboard of spots linked to their entity, mark them cleaned |
| WARD_OFFICER | View all spots in their ward, mark cleaned, reject fake reports |

## MVP scope (8-day deadline)
- JWT login/register, with demo-user quick-login buttons for each role
- Landing page, Haunted Map, Report form, Spot Details, My Profile, Entity Dashboard, Leaderboards
- Monster levels, cursed clusters, confirmations, cleanup, the civic score engine and a daily scheduled penalty job
- Seed data, Docker Compose for local dev, Render (backend) + Vercel/Netlify (frontend) deploy

## Out of scope
- Real payments or a real reward catalogue (points are display-only)
- Push, SMS or email notifications
- Real municipal integrations or official ward boundary polygons (wards are text labels)
- Native mobile apps (the web app is mobile-responsive instead)
- AI or image recognition to detect garbage
- Multiple cities at once (the data model allows it, but only Bengaluru is seeded)
- PostGIS or other spatial databases (we use Haversine in Java)

## Hard rules
- **No real company names, brands or trademarks** anywhere: code, seed data, UI, docs. Use generic names such as "Tech Park Alpha", "Green Meadows Society" or "Corner Kirana Store".
- Ask before adding any library that is not listed in `tech.md`.
