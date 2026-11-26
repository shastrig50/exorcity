package com.exorcity.spot;

import java.time.Duration;
import java.time.Instant;

/**
 * Pure monster-level rules (see domain.md).
 * Age 0–2 days = 1, 3–7 = 2, 8–14 = 3, 15–30 = 4, over 30 = 5; +1 per 5 confirmations; capped at 5.
 */
public final class MonsterLevelCalculator {

    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 5;
    public static final int CONFIRMATIONS_PER_LEVEL = 5;

    private MonsterLevelCalculator() {
    }

    public static long ageInDays(Instant createdAt, Instant now) {
        return Math.max(0, Duration.between(createdAt, now).toDays());
    }

    public static int baseLevel(long ageDays) {
        if (ageDays <= 2) return 1;
        if (ageDays <= 7) return 2;
        if (ageDays <= 14) return 3;
        if (ageDays <= 30) return 4;
        return 5;
    }

    public static int level(long ageDays, int confirmations) {
        int bonus = Math.max(0, confirmations) / CONFIRMATIONS_PER_LEVEL;
        return Math.min(MAX_LEVEL, baseLevel(Math.max(0, ageDays)) + bonus);
    }

    /** @return the monster level, or 0 when the spot has no monster (cleaned or rejected). */
    public static int levelOf(GarbageSpot spot, Instant now) {
        if (!spot.isOpen()) return 0;
        return level(ageInDays(spot.getCreatedAt(), now), spot.getConfirmationCount());
    }
}
