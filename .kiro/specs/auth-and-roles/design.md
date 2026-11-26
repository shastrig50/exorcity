# Design: auth-and-roles

## Library choice
`spring-boot-starter-oauth2-resource-server` (Spring Security's built-in Nimbus JWT support) instead of a third-party JWT library. It's approved under "Spring Security with JWT", has no extra vendor, and validation is handled by the resource-server filter.

## Backend
- `config/JwtConfig`: `SecretKey` from `exorcity.jwt.secret`, `NimbusJwtEncoder` (`ImmutableSecret`), `NimbusJwtDecoder.withSecretKey(...).macAlgorithm(HS256)`.
- `config/SecurityConfig`: stateless, CSRF off, CORS from `CORS_ALLOWED_ORIGINS`, `oauth2ResourceServer().jwt()` with a `JwtAuthenticationConverter` mapping claim `role` → `ROLE_<role>`; `@EnableMethodSecurity` for `@PreAuthorize`.
- `auth/JwtService.issue(User)` → token + expiry. `auth/AuthService` handles login/register. `auth/CurrentUser.id(jwt)` reads the subject.
- `UserDetailsServiceAutoConfiguration` is excluded (no in-memory default user).
- Errors: `BadCredentialsException` → 401, `AccessDeniedException` → 403 via `GlobalExceptionHandler`.

## Frontend
- `AuthService` (signals): session in localStorage (expired sessions dropped on load), `isCitizen/isEntityAdmin/isWardOfficer`, `canClean(spot)`, `homeFor(role)`.
- `authInterceptor` adds `Authorization: Bearer`; a 401 logs the user out.
- `authGuard`, `roleGuard(...roles)`.
- `LoginComponent`: reactive form, login/register toggle, 5 demo-user buttons (password `Demo@123`).

## Demo users (seeded)
| Role | Email |
|---|---|
| CITIZEN | citizen@exorcity.demo (+ 9 more citizens) |
| SOCIETY_ADMIN | society@exorcity.demo (Green Meadows Society) |
| CORPORATE_ADMIN | corporate@exorcity.demo (Tech Park Alpha) |
| SHOP_OWNER | shop@exorcity.demo (Corner Kirana Store) |
| WARD_OFFICER | ward@exorcity.demo (Koramangala) |
