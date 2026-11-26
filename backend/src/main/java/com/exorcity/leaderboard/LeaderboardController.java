package com.exorcity.leaderboard;

import com.exorcity.common.PageResponse;
import com.exorcity.entity.EntityType;
import com.exorcity.leaderboard.LeaderboardDtos.CitizenEntry;
import com.exorcity.leaderboard.LeaderboardDtos.EntityEntry;
import com.exorcity.leaderboard.LeaderboardDtos.WardScore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Leaderboards")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard/citizens")
    @Operation(summary = "Top ghost hunters by civic score")
    public PageResponse<CitizenEntry> citizens(@PageableDefault(size = 10) Pageable pageable) {
        return leaderboardService.citizens(pageable);
    }

    @GetMapping("/leaderboard/entities")
    @Operation(summary = "Cleanest entities by civic score (optional type filter)")
    public PageResponse<EntityEntry> entities(@RequestParam(required = false) EntityType type,
                                              @PageableDefault(size = 10) Pageable pageable) {
        return leaderboardService.entities(type, pageable);
    }

    @GetMapping("/wards/haunted-scores")
    @Operation(summary = "Most haunted wards")
    public List<WardScore> wardHauntedScores() {
        return leaderboardService.wardHauntedScores();
    }
}
