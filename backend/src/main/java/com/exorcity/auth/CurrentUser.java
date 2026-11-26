package com.exorcity.auth;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;

/** Reads the caller's user id from the JWT subject. */
public final class CurrentUser {

    private CurrentUser() {
    }

    /** @return the user id, or null for anonymous callers. */
    public static Long idOrNull(Jwt jwt) {
        return jwt == null ? null : Long.valueOf(jwt.getSubject());
    }

    public static Long id(Jwt jwt) {
        if (jwt == null) throw new AuthenticationCredentialsNotFoundException("Login required");
        return Long.valueOf(jwt.getSubject());
    }
}
