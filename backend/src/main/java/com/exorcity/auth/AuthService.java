package com.exorcity.auth;

import com.exorcity.auth.AuthDtos.AuthResponse;
import com.exorcity.auth.AuthDtos.LoginRequest;
import com.exorcity.auth.AuthDtos.RegisterRequest;
import com.exorcity.auth.AuthDtos.UserSummary;
import com.exorcity.common.exception.ConflictException;
import com.exorcity.user.Role;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmailIgnoreCase(req.email().trim())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        return respond(user);
    }

    /** Self-registration always creates a CITIZEN; admin roles are provisioned, not self-assigned. */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = users.save(User.builder()
                .name(req.name().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(Role.CITIZEN)
                .createdAt(Instant.now(clock))
                .build());
        return respond(user);
    }

    private AuthResponse respond(User user) {
        JwtService.IssuedToken token = jwtService.issue(user);
        return new AuthResponse(token.token(), token.expiresAt(), UserSummary.from(user));
    }
}
