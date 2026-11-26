package com.exorcity.spot;

import com.exorcity.common.GeoUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Pure rule: when 3+ OPEN spots reported in the last 30 days lie within 50 m of a spot,
 * all of them are cursed.
 */
public final class CurseDetector {

    public static final double CURSE_RADIUS_METERS = 50d;
    public static final int CURSE_MIN_SPOTS = 3;
    public static final Duration CURSE_WINDOW = Duration.ofDays(30);

    private CurseDetector() {
    }

    /** @return the cluster around {@code center} (including it), or an empty list when it is not cursed. */
    public static List<GarbageSpot> cursedCluster(GarbageSpot center, Collection<GarbageSpot> candidates, Instant now) {
        Instant since = now.minus(CURSE_WINDOW);
        List<GarbageSpot> cluster = new ArrayList<>();
        cluster.add(center);
        for (GarbageSpot s : candidates) {
            if (s == center || (s.getId() != null && s.getId().equals(center.getId()))) continue;
            if (!s.isOpen() || s.getCreatedAt().isBefore(since)) continue;
            double d = GeoUtils.distanceMeters(center.getLatitude(), center.getLongitude(), s.getLatitude(), s.getLongitude());
            if (d <= CURSE_RADIUS_METERS) cluster.add(s);
        }
        boolean centerEligible = center.isOpen() && !center.getCreatedAt().isBefore(since);
        return centerEligible && cluster.size() >= CURSE_MIN_SPOTS ? cluster : List.of();
    }
}
