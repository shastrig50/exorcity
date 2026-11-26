# Design: garbage-reporting

## Backend
- `spot/GarbageSpot` (JPA), `GarbageType` (monster name + emoji), `SpotStatus` (OPEN, CLEANED, REJECTED).
- `common/GeoUtils.distanceMeters` (Haversine, R = 6,371,000 m), plus `latDelta/lngDelta` for bounding-box prefilters.
- `entity/EntityLinker` (pure): `nearest(...)`, `nearestWithinRadius(..., 200 m)`.
- `SpotService.create`: load entities → link → choose ward → save → `CurseService.evaluate` (see cursed-spots).
- `SpotSpecifications.filter(status, ward, bbox)` → `JpaSpecificationExecutor` + `Pageable`.
- `SpotMapper` loads reporter, cleaner and entity names in bulk (no N+1 queries) and computes `ageDays`, `monsterLevel` and `confirmedByMe`.
- `photo/PhotoService`: parses `cloudinary://key:secret@cloud` and makes a signed upload via `RestClient` (SHA-1 signature of `folder&timestamp` + secret). No Cloudinary SDK needed.

## Frontend
- `ReportComponent`: radio cards per monster type, `PhotoInputComponent` (upload if enabled, URL, "Use a sample photo"), a Leaflet map with a click-to-pin circle marker, and a GPS button via `navigator.geolocation`.
- Sample photos are SVG illustrations in `public/seed/`.

## Testing
- `EntityLinkerProperties` (jqwik): never links beyond 200 m; when something is in range, it links the nearest.
