---
inclusion: always
---

# Domain Model & Business Rules

## Entities
**User**: id, name, email (unique), passwordHash (BCrypt), role, civicScore (default 600), entityId (nullable; set for entity admins), ward (nullable; set for ward officers).

**Role**: `CITIZEN`, `SOCIETY_ADMIN`, `CORPORATE_ADMIN`, `SHOP_OWNER`, `WARD_OFFICER`.

**Entity** (a responsible organisation; called `ResponsibleEntity` in Java to avoid a clash with JPA's `@Entity`): id, name, type (`SOCIETY`, `CORPORATE_CAMPUS`, `SHOP`), latitude, longitude, ward, civicScore (default 600).

**GarbageSpot**: id, type, description, latitude, longitude, beforePhotoUrl, afterPhotoUrl, status (`OPEN`, `CLEANED`, `REJECTED`), reportedBy (userId), cleanedBy (userId, nullable), linkedEntityId (nullable), ward, createdAt, cleanedAt, confirmationCount, cursed (boolean), plus bookkeeping flags `firstConfirmationRewarded`, `cursePenaltyApplied`, `overduePenaltyApplied` (each stops a rule from firing twice).
> `REJECTED` is an MVP addition so that a fake report disappears from the map but stays auditable.

**Confirmation**: id, spotId, userId, createdAt. Unique on (spotId, userId). The reporter cannot confirm their own spot.

**ScoreEvent**: id, userId (nullable), entityId (nullable; exactly one of the two is set), points (signed), reason (enum + text), spotId (nullable), createdAt. This is the audit trail for **every** score change.

## Garbage types → monsters
| Type | Monster | Emoji |
|---|---|---|
| ROADSIDE_PILE | Trash Zombie | 🧟 |
| OVERFLOWING_BIN | Bin Beast | 👹 |
| PLASTIC_WASTE | Plastic Phantom | 👻 |
| ILLEGAL_DUMP | Dump Demon | 🕷️ |
| LAKE_OR_DRAIN | Swamp Ghoul | 🐸 |

## Monster level (1–5), OPEN spots only
Age in whole days = floor((now − createdAt) / 24h).
- 0–2 days → 1, 3–7 → 2, 8–14 → 3, 15–30 → 4, > 30 → 5
- +1 level per 5 confirmations (`confirmationCount / 5`), capped at 5
- CLEANED or REJECTED → no monster (level `null` / 0)
- Invariants: always 1..5 for OPEN spots; never decreases as age increases (with the same confirmations); never decreases as confirmations increase.

## Entity linking
When a spot is created, find the nearest Entity by Haversine distance (earth radius 6,371,000 m). If it is **≤ 200 m**, set `linkedEntityId` and use that entity's ward if the request has none. Otherwise leave it unlinked (ward responsibility).
Invariant: a spot is never linked to an entity more than 200 m away.

## Cursed spots
When a spot is created, collect the OPEN spots created in the **last 30 days** within **50 m** of the new spot (including itself). If there are **≥ 3**, mark all of them `cursed = true`. For each linked entity whose spot *newly* becomes cursed, apply −50 (once per spot).
Cursed spots show a pulsing red aura. A cursed flag stays until the spot is cleaned.

## Civic Score
Range **0–900**, always clamped. Every change writes a ScoreEvent with the *actually applied* delta.

**Citizen**
| Event | Points |
|---|---|
| Their report gets its first confirmation | +10 (to the reporter, once per spot) |
| Confirming someone else's spot | +5 (to the confirmer) |
| Cleaning a spot with an after photo | +25 (to the cleaner) |
| Report rejected as fake by a ward officer | −30 (to the reporter) |

**Entity**
| Event | Points |
|---|---|
| Linked spot cleaned within 3 days of creation | +20 |
| Linked spot open longer than 7 days | −15 per spot, applied by the **daily scheduled job** (02:00 Asia/Kolkata), **once per spot** (`overduePenaltyApplied`), so scores don't drain to 0 every day |
| Linked spot becomes cursed | −50 (once per spot) |

**Rating**
| Score | Rating |
|---|---|
| ≥ 750 | Ghost Free |
| 600–749 | Calm Spirits |
| 400–599 | Haunted |
| < 400 | Cursed |

**Reward points** = max(0, civicScore − 600) / 10 (integer division). These are for display only; there are no payments.

Invariants: the score stays within [0, 900] for any sequence of events. Cleaning never lowers the cleaner's score.

## Permissions
- **Report / confirm / clean:** CITIZEN (any logged-in user can view).
- **Clean linked spots:** SOCIETY_ADMIN / CORPORATE_ADMIN / SHOP_OWNER, only for spots where `linkedEntityId == user.entityId`.
- **Clean / reject:** WARD_OFFICER, only for spots where `spot.ward == user.ward`.
- Cleaning needs an `afterPhotoUrl`. A cleaner who is an entity admin or ward officer still gets the +25 as a user.
- Only OPEN spots can be confirmed, cleaned or rejected; otherwise the API returns 409 Conflict.

## Ward haunted score
For each ward: `hauntedScore = Σ monsterLevel of OPEN spots + 2 × cursed OPEN spots`. A higher score means a more haunted ward.
