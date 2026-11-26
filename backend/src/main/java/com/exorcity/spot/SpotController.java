package com.exorcity.spot;

import com.exorcity.auth.CurrentUser;
import com.exorcity.common.PageResponse;
import com.exorcity.spot.SpotDtos.CleanSpotRequest;
import com.exorcity.spot.SpotDtos.CreateSpotRequest;
import com.exorcity.spot.SpotDtos.SpotActionResponse;
import com.exorcity.spot.SpotDtos.SpotResponse;
import com.exorcity.spot.SpotDtos.SpotStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
@Tag(name = "Spots")
public class SpotController {

    private final SpotService spotService;

    @GetMapping
    @Operation(summary = "List spots (filter by status, ward, bbox=minLng,minLat,maxLng,maxLat)")
    public PageResponse<SpotResponse> list(
            @RequestParam(required = false) SpotStatus status,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String bbox,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        return spotService.list(status, ward, bbox, pageable, CurrentUser.idOrNull(jwt));
    }

    @GetMapping("/stats")
    @Operation(summary = "Live monster counts for the landing page")
    public SpotStats stats() {
        return spotService.stats();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Spot details with monster level")
    public SpotResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return spotService.get(id, CurrentUser.idOrNull(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CITIZEN')")
    @Operation(summary = "Report a garbage spot (auto-links the nearest entity within 200 m)")
    public SpotResponse create(@Valid @RequestBody CreateSpotRequest request, @AuthenticationPrincipal Jwt jwt) {
        return spotService.create(CurrentUser.id(jwt), request);
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('CITIZEN')")
    @Operation(summary = "Confirm someone else's sighting (+5, reporter +10 on first)")
    public SpotActionResponse confirm(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return spotService.confirm(id, CurrentUser.id(jwt));
    }

    @PostMapping("/{id}/clean")
    @Operation(summary = "Exorcise a spot with an after photo (+25)")
    public SpotActionResponse clean(@PathVariable Long id, @Valid @RequestBody CleanSpotRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {
        return spotService.clean(id, CurrentUser.id(jwt), request);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('WARD_OFFICER')")
    @Operation(summary = "Reject a fake report (reporter −30)")
    public SpotActionResponse reject(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return spotService.reject(id, CurrentUser.id(jwt));
    }
}
