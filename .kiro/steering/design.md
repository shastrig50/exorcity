---
inclusion: always
---

# Spooky Design System

**Guiding principle:** the spooky look has to *improve the function*. Scarier monster = more urgent problem. Every effect has to carry information (urgency, status, success), and none of them may hurt readability.

## Colour tokens (Tailwind `theme.extend.colors`)
| Token | Hex | Use |
|---|---|---|
| `night` | `#0b0b0f` | page background |
| `crypt` | `#16161d` | cards, panels, map overlays |
| `pumpkin` | `#ff6b00` | primary actions, focus rings, links |
| `blood` | `#8b0000` | danger, cursed, high-level monsters (with light text on top) |
| `ecto` | `#39ff14` | accents only: success, "exorcised", score glow. Never body text |
| `bone` | `#e8e6e3` | main text |
| `mist` | `#a8a6a3` | secondary text (AA on `night` and `crypt`) |

Contrast rules: `bone` on `night`/`crypt` is well above AA. Use `pumpkin` on `night` for text of 18px or larger, or bold; for pumpkin buttons use `night`-coloured text on a `pumpkin` fill. Text on `blood` must be `bone`. Never put `ecto` text on a light background.

## Typography
- **Creepster** (Google Fonts): logo, hero headline and page titles (h1) only.
- **Inter**: everything else, including body, forms, tables, buttons and map popups. Base size 16px, line-height 1.5.

## Monster scale (urgency language)
| Level | Size | Colour / effect | Meaning |
|---|---|---|---|
| 1 | 28px | bone, gentle bob | new (0–2 days) |
| 2 | 34px | pumpkin tint | lingering (3–7 days) |
| 3 | 42px | deep orange, slow pulse | festering (8–14 days) |
| 4 | 50px | blood red glow, faster pulse | rotting (15–30 days) |
| 5 | 60px | blood red, strong pulse + shake | ancient horror (30+ days) |
| cursed | +aura | pulsing red ring around the icon | cluster of 3+ spots within 50 m |

Monster emoji by type: Trash Zombie 🧟, Bin Beast 👹, Plastic Phantom 👻, Dump Demon 🕷️, Swamp Ghoul 🐸.
A legend on the map explains size, colour and aura, so meaning never relies on colour alone.

## Effects
- **Fog:** slow, drifting semi-transparent gradient layers on the landing hero.
- **Monster pulse/bob:** CSS keyframes. Speed and intensity scale with level.
- **Exorcism smoke:** when a spot is cleaned, the monster scales up, fades and blurs into rising grey/ecto smoke puffs (about 1.2s), then a "Exorcised! +25" toast appears.
- **Candle loader:** a flickering flame for loading states, with an `aria-label="Loading"` and `role="status"`.
- **Score meter:** a semicircular gauge from 0 to 900 with a glow coloured by rating.

## Rating badges
| Score | Rating | Colour |
|---|---|---|
| 750+ | Ghost Free | ecto |
| 600–749 | Calm Spirits | pumpkin |
| 400–599 | Haunted | orange-red |
| < 400 | Cursed | blood |

## Accessibility (non-negotiable)
- WCAG 2.1 AA contrast for all text and UI controls.
- Visible focus state on every interactive element: `focus-visible:ring-2 ring-pumpkin ring-offset-2 ring-offset-night`.
- Every icon-only button and every monster marker has an `aria-label` (e.g. "Bin Beast, level 4, open 18 days, cursed").
- Every `<img>` has meaningful `alt` text (e.g. "Before photo of overflowing bin near Green Meadows Society").
- Forms: real `<label>`s, errors linked via `aria-describedby`, `aria-live="polite"` for toasts.
- **Reduced motion:** respect `prefers-reduced-motion`, plus an in-app toggle (saved to localStorage) that adds a `.reduce-motion` class on `<html>` and turns off fog, pulse, shake and smoke. Smoke becomes a simple fade.
- Keyboard: the map has a "List view" alternative with all spots as a sortable table, and monsters can be reached by keyboard.
- Mobile-first responsive layout. The navigation collapses into a menu below 768px, and touch targets are at least 44px.
