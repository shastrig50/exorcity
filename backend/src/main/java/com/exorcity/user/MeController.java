package com.exorcity.user;

import com.exorcity.auth.CurrentUser;
import com.exorcity.user.ProfileService.MeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Me")
public class MeController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "My profile: civic score, rating, reward points and score history")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt) {
        return profileService.me(CurrentUser.id(jwt));
    }
}
