package com.exorcity.score;

import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.spot.GarbageSpot;
import com.exorcity.spot.GarbageSpotRepository;
import com.exorcity.spot.SpotStatus;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Daily: −15 to the linked entity for every spot that has been open longer than 7 days (once per spot). */
@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueSpotJob {

    public static final Duration OVERDUE_AFTER = Duration.ofDays(7);

    private final GarbageSpotRepository spots;
    private final ResponsibleEntityRepository entities;
    private final CivicScoreService scores;
    private final Clock clock;

    @Scheduled(cron = "${exorcity.jobs.overdue-cron:0 0 2 * * *}", zone = "Asia/Kolkata")
    public void scheduledRun() {
        int penalised = run();
        log.info("Overdue job penalised {} spot(s)", penalised);
    }

    /** @return number of spots penalised in this run. */
    @Transactional
    public int run() {
        Instant now = Instant.now(clock);
        List<GarbageSpot> overdue = spots
                .findByStatusAndLinkedEntityIdIsNotNullAndOverduePenaltyAppliedFalseAndCreatedAtBefore(
                        SpotStatus.OPEN, now.minus(OVERDUE_AFTER));
        for (GarbageSpot spot : overdue) {
            entities.findById(spot.getLinkedEntityId())
                    .ifPresent(e -> scores.applyToEntity(e, ScoreReason.LINKED_SPOT_OVERDUE, spot.getId(), now));
            spot.setOverduePenaltyApplied(true);
        }
        spots.saveAll(overdue);
        return overdue.size();
    }
}
