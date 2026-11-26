# Requirements: confirmations-and-cleanup

## Introduction
Citizens confirm sightings; responsible parties clean spots with an after photo; ward officers reject fake reports. Cleaning plays a smoke exorcism animation.

### R1 Confirm
**User story:** As a citizen, I want to confirm a sighting so that urgent spots grow scarier and get attention.
1. WHEN a CITIZEN confirms an OPEN spot via `POST /api/spots/{id}/confirm` THEN the system SHALL store one Confirmation, increment `confirmationCount` and award the confirmer +5.
2. WHEN it is the spot's first confirmation THEN the system SHALL award the reporter +10 (only once per spot).
3. IF the caller is the reporter, or has already confirmed, THEN the system SHALL respond 409.

### R2 Clean
1. WHEN an authorized user posts `POST /api/spots/{id}/clean` with an `afterPhotoUrl` THEN the system SHALL mark the spot CLEANED with `cleanedAt` and `cleanedBy`, remove its curse and award the cleaner +25.
2. Authorized cleaners: any CITIZEN; an entity admin for spots linked to their entity; a ward officer for spots in their ward. Otherwise 403.
3. IF the spot is linked and was cleaned within 3 days of creation THEN the linked entity SHALL gain +20.
4. Cleaning SHALL never lower the cleaner's score. *(property test)*
5. IF the spot is not OPEN THEN the system SHALL respond 409.

### R3 Reject
1. WHEN the ward officer of the spot's ward posts `POST /api/spots/{id}/reject` THEN the system SHALL mark it REJECTED and the reporter SHALL lose 30.
2. Officers of other wards and other roles SHALL get 403.

### R4 UI
1. The spot page SHALL show Confirm / Exorcise / Reject actions only to users allowed to perform them, with a reason when an action is unavailable (e.g. "You reported this one").
2. WHEN a spot is cleaned THEN the monster SHALL grow, blur and fade into rising smoke (about 1.2 s), then the cleaned state and a before/after slider SHALL appear, along with a toast showing the points earned.
3. With reduced motion, the smoke SHALL become a simple fade.
