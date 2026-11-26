package com.exorcity.score;

/** Pure Civic Score rules: clamping, rating bands and reward points. */
public final class CivicScorePolicy {

    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 900;
    public static final int DEFAULT_SCORE = 600;

    private CivicScorePolicy() {
    }

    public static int clamp(long score) {
        return (int) Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    /** Applies a delta and clamps the result to [0, 900]. */
    public static int apply(int current, int delta) {
        return clamp((long) clamp(current) + delta);
    }

    public static Rating rating(int score) {
        if (score >= 750) return Rating.GHOST_FREE;
        if (score >= 600) return Rating.CALM_SPIRITS;
        if (score >= 400) return Rating.HAUNTED;
        return Rating.CURSED;
    }

    /** Redeemable points: every 10 points above the 600 baseline is one reward point. */
    public static int rewardPoints(int score) {
        return Math.max(0, clamp(score) - DEFAULT_SCORE) / 10;
    }
}
