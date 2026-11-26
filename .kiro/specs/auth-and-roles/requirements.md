# Requirements: auth-and-roles

## Introduction
Stateless JWT authentication with five roles, demo users and a login page with one quick-login button per role.

### R1 Registration
**User story:** As a citizen, I want to create an account so that I can report and clean garbage.
1. WHEN a visitor submits a valid name, email and 8–72 character password to `POST /api/auth/register` THEN the system SHALL create a CITIZEN with civic score 600 and return a JWT.
2. IF the email already exists THEN the system SHALL respond 409.
3. The system SHALL never allow self-registration with an admin or officer role.
4. The system SHALL store passwords only as BCrypt hashes.

### R2 Login
1. WHEN valid credentials are posted to `POST /api/auth/login` THEN the system SHALL return a signed HS256 JWT (subject = user id, claim `role`) and the user summary.
2. IF credentials are invalid THEN the system SHALL respond 401 with "Invalid email or password".
3. The token SHALL expire after `JWT_EXPIRATION_MINUTES`; the secret SHALL come from `JWT_SECRET` (≥ 32 chars, else startup fails).

### R3 Authorization
1. Public: health, config, auth, Swagger, `GET` spots/entities/leaderboards/wards.
2. WHEN a protected endpoint is called without a valid token THEN the system SHALL respond 401.
3. WHEN a user lacks the role for an action THEN the system SHALL respond 403.
4. Report and confirm SHALL require CITIZEN; reject SHALL require WARD_OFFICER; clean rules per domain.md.

### R4 Frontend
1. The login page SHALL offer a quick-login button for each role (CITIZEN, SOCIETY_ADMIN, CORPORATE_ADMIN, SHOP_OWNER, WARD_OFFICER).
2. WHEN logged in THEN the app SHALL attach the bearer token to API calls and show the user's name and score in the header.
3. WHEN the API returns 401 for a stored token THEN the app SHALL log the user out.
4. Route guards SHALL redirect anonymous users to `/login?next=…` and wrong roles to their home page.
