package com.exorcity.score;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs")
public class JobController {

    private final OverdueSpotJob overdueSpotJob;

    @PostMapping("/overdue-penalties")
    @PreAuthorize("hasRole('WARD_OFFICER')")
    @Operation(summary = "Run the daily overdue-spot penalty job now (demo helper)")
    public Map<String, Integer> runOverdueJob() {
        return Map.of("penalisedSpots", overdueSpotJob.run());
    }
}
