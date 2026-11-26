# Demo video

| File | What |
|---|---|
| `exorcity-demo.mp4` | Final demo: 2:26, 1920×1080, narrated (Microsoft Zira TTS) |
| `exorcity-demo.srt` | English captions with timing, for YouTube |
| `thumbnail.png` | 1280×720 YouTube thumbnail |
| `YOUTUBE.md` | Title, description with chapters, tags, upload steps |
| `narration.json` | The narration script, one line per scene |
| `cards/` | Title, "Built with Kiro" and outro cards (HTML) |

## Scene list
| Time | Scene | On screen |
|---|---|---|
| 0:00 | Title | Exorcity title card with fog and the five monsters |
| 0:07 | Problem | Landing page: live monster count, bestiary, urgency scale |
| 0:19 | Haunted map | Start Hunting → dark map → select a level 5 "Ancient horror" |
| 0:35 | Cursed | Deep link to the cursed cluster behind Green Meadows Society |
| 0:47 | Accessibility | Sortable list view, motion toggle |
| 0:54 | Demo login | One-click login as Asha (citizen) |
| 1:01 | Report | Type, typed description, sample photo, GPS pin → auto-linked to Silver Oak Apartments |
| 1:14 | Confirm | Confirm another hunter's sighting (+5) |
| 1:22 | Exorcise | After photo → smoke animation → +25 → before/after slider sweep |
| 1:32 | Profile | Civic Score meter, rating, reward points, history |
| 1:41 | Society | Green Meadows dashboard: Cursed rating, 4 cursed spots, score history |
| 1:53 | Ward officer | Koramangala: every open spot, scariest first |
| 1:59 | Leaderboards | Ghost hunters, most haunted wards, societies, campuses |
| 2:06 | Kiro | Steering, specs, hooks, property tests |
| 2:19 | Outro | Tagline, repo URL, demo password |

## How it was made
The video was recorded automatically from the running app (headless Chrome via the DevTools screencast API, with a
scripted cursor), narrated with Windows text-to-speech, and assembled with ffmpeg. Each scene's actions are timed to
its narration clip. To re-record, reset the demo data first (`docker compose down -v && docker compose up -d`, then
restart the backend) so the walkthrough starts from the seeded state.
