package com.exorcity.auth;

import com.exorcity.user.Role;
import com.exorcity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 72, message = "must be 8-72 characters") String password) {
    }

    public record UserSummary(Long id, String name, String email, Role role, int civicScore, Long entityId,
                              String ward) {
        public static UserSummary from(User u) {
            return new UserSummary(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCivicScore(),
                    u.getEntityId(), u.getWard());
        }
    }

    public record AuthResponse(String token, Instant expiresAt, UserSummary user) {
    }
}
