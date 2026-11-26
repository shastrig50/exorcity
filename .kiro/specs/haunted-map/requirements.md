# Requirements: haunted-map

## Introduction
The main page: a dark map where every open spot is a monster whose size, colour and motion show how urgent it is.

### R1 Monster level
1. The system SHALL compute the level of an OPEN spot from its age in whole days: 0–2 → 1, 3–7 → 2, 8–14 → 3, 15–30 → 4, > 30 → 5.
2. The system SHALL add +1 level per 5 confirmations, capped at 5.
3. A CLEANED or REJECTED spot SHALL have no monster (level 0).
4. The level SHALL always be in 1..5 for OPEN spots and SHALL never decrease as age or confirmations increase. *(property tests)*

### R2 Map
1. WHEN the map page opens THEN the app SHALL show a dark OpenStreetMap-based Leaflet map of Bengaluru with every OPEN spot as an animated monster icon.
2. The icon size and colour SHALL scale with level; cursed spots SHALL show a pulsing red aura.
3. WHEN a monster is clicked or activated with Enter THEN the app SHALL show its photo, level, age, owner and a link to its detail page.
4. The app SHALL show a legend explaining level sizes and the cursed aura (meaning is never shown by colour alone).
5. The app SHALL offer type filters and a sortable **list view** as a keyboard and screen-reader alternative.
6. Every marker SHALL have an aria-label such as "Bin Beast, level 4 rotting, open 18 days, cursed".

### R3 Spot details
1. The details page SHALL show the photo (or a before/after slider when cleaned), monster level, age, linked entity, ward and confirmations.

### R4 Motion
1. Animations SHALL stop when `prefers-reduced-motion` is set or the in-app motion toggle is off.
