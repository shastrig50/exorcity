package com.exorcity.spot;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class CurseDetectorTest {

    private static final Instant NOW = Instant.parse("2026-11-26T00:00:00Z");
    private long ids = 1;

    private GarbageSpot spot(double lat, double lng, int ageDays, SpotStatus status) {
        return GarbageSpot.builder().id(ids++).type(GarbageType.ROADSIDE_PILE).latitude(lat).longitude(lng)
                .status(status).createdAt(NOW.minus(Duration.ofDays(ageDays))).build();
    }

    @Test
    void threeOpenRecentSpotsWithin50mAreCursed() {
        GarbageSpot a = spot(12.93600, 77.62500, 1, SpotStatus.OPEN);
        GarbageSpot b = spot(12.93620, 77.62510, 5, SpotStatus.OPEN);   // ~25 m
        GarbageSpot c = spot(12.93590, 77.62480, 10, SpotStatus.OPEN);  // ~25 m
        assertThat(CurseDetector.cursedCluster(a, List.of(a, b, c), NOW)).containsExactlyInAnyOrder(a, b, c);
    }

    @Test
    void twoSpotsAreNotEnough() {
        GarbageSpot a = spot(12.93600, 77.62500, 1, SpotStatus.OPEN);
        GarbageSpot b = spot(12.93620, 77.62510, 5, SpotStatus.OPEN);
        assertThat(CurseDetector.cursedCluster(a, List.of(a, b), NOW)).isEmpty();
    }

    @Test
    void ignoresFarAwayOldAndCleanedSpots() {
        GarbageSpot a = spot(12.93600, 77.62500, 1, SpotStatus.OPEN);
        GarbageSpot far = spot(12.93700, 77.62500, 1, SpotStatus.OPEN);       // ~111 m
        GarbageSpot old = spot(12.93605, 77.62505, 45, SpotStatus.OPEN);      // > 30 days
        GarbageSpot cleaned = spot(12.93595, 77.62495, 2, SpotStatus.CLEANED);
        assertThat(CurseDetector.cursedCluster(a, List.of(a, far, old, cleaned), NOW)).isEmpty();
    }
}
