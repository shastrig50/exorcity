package com.exorcity.entity;

import com.exorcity.auth.CurrentUser;
import com.exorcity.common.PageResponse;
import com.exorcity.entity.EntityDtos.DashboardResponse;
import com.exorcity.entity.EntityDtos.EntityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
@Tag(name = "Entities")
public class EntityController {

    private final EntityService entityService;

    @GetMapping
    @Operation(summary = "List responsible entities (societies, campuses, shops) by civic score")
    public PageResponse<EntityResponse> list(@RequestParam(required = false) EntityType type,
                                             @PageableDefault(size = 20) Pageable pageable) {
        return entityService.list(type, pageable);
    }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Entity dashboard: linked spots, score and history (entity admins / ward officer)")
    public DashboardResponse dashboard(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return entityService.dashboard(id, CurrentUser.id(jwt));
    }
}
