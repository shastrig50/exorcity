# Design: haunted-map

## Backend
- `spot/MonsterLevelCalculator` (pure): `ageInDays`, `baseLevel`, `level(age, confirmations)`, `levelOf(spot, now)`. Uses the injected `Clock`.
- `SpotResponse` carries `monsterLevel`, `ageDays`, `monsterName`, `monsterEmoji` and `cursed`, so the frontend never re-implements the rules.

## Frontend
- `MapComponent`: Leaflet outside the Angular zone; OSM tiles darkened with a CSS filter (`invert + hue-rotate`), with attribution kept.
  - Markers are `L.divIcon` with `<span class="monster lvl-N [cursed]">emoji</span>` and `keyboard: true`; `aria-label` and `role=button` are set on add; z-index grows with level so the scariest monsters render on top.
  - Selecting a marker opens a side panel (not a Leaflet popup) so Angular routing and focus work.
  - Type filter chips (checkboxes), list view table with sortable level/age/confirmations.
- Global CSS (`styles.css`): `.monster.lvl-1..5` sizes 28/34/42/50/60 px, colour bone → pumpkin → blood, bob → pulse → pulse+shake, `.cursed::after` aura.
- `MonsterAvatarComponent` reuses the same classes for the legend, lists and details.
- `SpotDetailComponent` + `BeforeAfterSliderComponent` (range input clips the before image).

## Testing
- `MonsterLevelProperties` (jqwik): range 1..5; monotonic in age; monotonic in confirmations; examples for each age band.
