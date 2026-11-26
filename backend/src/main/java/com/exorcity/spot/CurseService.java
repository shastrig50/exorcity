package com.exorcity.spot;

import com.exorcity.common.GeoUtils;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.score.CivicScoreService;
import com.exorcity.score.ScoreReason;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurseService {

    private final GarbageSpotRepository spots;
    private final ResponsibleEntityRepository entities;
    private final CivicScoreService scores;

    /**
     * Curses the cluster around {@code spot} if it qualifies, applying −50 once per newly cursed linked spot.
     * @return the number of spots that became cursed in this call.
     */
    @Transactional
    public int evaluate(GarbageSpot spot, Instant now) {
        double r = CurseDetector.CURSE_RADIUS_METERS;
        double dLat = GeoUtils.latDelta(r);
        double dLng = GeoUtils.lngDelta(r, spot.getLatitude());
        List<GarbageSpot> candidates = spots.findByStatusAndCreatedAtAfterAndLatitudeBetweenAndLongitudeBetween(
                SpotStatus.OPEN, now.minus(CurseDetector.CURSE_WINDOW),
                spot.getLatitude() - dLat, spot.getLatitude() + dLat,
                spot.getLongitude() - dLng, spot.getLongitude() + dLng);

        List<GarbageSpot> cluster = CurseDetector.cursedCluster(spot, candidates, now);
        int newlyCursed = 0;
        for (GarbageSpot s : cluster) {
            if (!s.isCursed()) {
                s.setCursed(true);
                newlyCursed++;
            }
            if (s.getLinkedEntityId() != null && !s.isCursePenaltyApplied()) {
                entities.findById(s.getLinkedEntityId())
                        .ifPresent(e -> scores.applyToEntity(e, ScoreReason.LINKED_SPOT_CURSED, s.getId(), now));
                s.setCursePenaltyApplied(true);
            }
        }
        spots.saveAll(cluster);
        return newlyCursed;
    }
}
