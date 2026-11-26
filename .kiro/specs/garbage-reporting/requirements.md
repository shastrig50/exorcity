# Requirements: garbage-reporting

## Introduction
Citizens report garbage with a type, description, photo and location. Each report is linked to the nearest responsible entity within 200 m.

### R1 Create a report
**User story:** As a citizen, I want to report garbage so that it shows up on the map and someone is held responsible.
1. WHEN a CITIZEN posts a valid `POST /api/spots` (type, latitude −90..90, longitude −180..180, beforePhotoUrl, optional description ≤ 1000) THEN the system SHALL create an OPEN spot with `createdAt = now` and return it with status 201.
2. IF required fields are missing or invalid THEN the system SHALL respond 400 with field errors.
3. IF the caller is not a CITIZEN THEN the system SHALL respond 403.

### R2 Entity linking
1. WHEN a spot is created THEN the system SHALL compute the Haversine distance to every entity and link the nearest one IF it is ≤ 200 m away.
2. IF no entity is within 200 m THEN the system SHALL leave `linkedEntityId` empty (ward responsibility).
3. The system SHALL never link a spot to an entity more than 200 m away. *(property test)*
4. WHEN no ward is supplied THEN the system SHALL use the ward of the nearest entity.

### R3 Photos
1. WHERE `CLOUDINARY_URL` is set, the system SHALL accept `POST /api/photos` (multipart image ≤ 5 MB) and return a hosted URL.
2. WHERE it is not set, the system SHALL report `photoUploadsEnabled: false` on `GET /api/config` and accept an image URL string.
3. Photo URLs SHALL be `http(s)://…` or a site-relative path.

### R4 Listing
1. `GET /api/spots` SHALL support optional `status`, `ward` and `bbox=minLng,minLat,maxLng,maxLat` filters with pagination (default 20, max 100).
2. `GET /api/spots/{id}` SHALL return the spot or 404.

### R5 Report page
1. The page SHALL let the user pick a type (shown with its monster), enter a description, provide a photo (upload / URL / sample) and set the location with GPS or by clicking the map.
2. WHEN submitted THEN the app SHALL show which entity was linked (or that the ward is responsible) and open the new spot.
