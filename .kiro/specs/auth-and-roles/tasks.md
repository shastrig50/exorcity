# Tasks: auth-and-roles

- [x] 1. Add `spring-boot-starter-oauth2-resource-server`; `JwtConfig` (HS256 encoder/decoder, secret length check) — _R2.3_
- [x] 2. `SecurityConfig`: public vs protected routes, role claim converter, method security, BCrypt — _R3_
- [x] 3. `User`, `Role`, `UserRepository` — _R1_
- [x] 4. `JwtService`, `AuthService`, `AuthController` (`/api/auth/login`, `/api/auth/register`) + DTOs with validation — _R1, R2_
- [x] 5. `GlobalExceptionHandler`: 401/403/409 mappings — _R1.2, R2.2, R3_
- [x] 6. `HealthControllerTest`: health public, `/api/me` without token → 401 — _R3.2_
- [x] 7. Frontend `AuthService`, `authInterceptor`, `authGuard`/`roleGuard` — _R4.2–R4.4_
- [x] 8. Login page with register toggle and 5 demo-role buttons — _R4.1_
- [x] 9. Header shows the user's name and score, plus log out; nav links depend on role
