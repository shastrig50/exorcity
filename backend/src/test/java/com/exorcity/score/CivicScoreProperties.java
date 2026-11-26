package com.exorcity.score;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

class CivicScoreProperties {

    @Property
    void scoreStaysWithinBoundsForAnySequenceOfEvents(
            @ForAll @IntRange(min = 0, max = 900) int start,
            @ForAll @Size(max = 300) List<ScoreReason> events) {
        int score = start;
        for (ScoreReason reason : events) {
            score = CivicScorePolicy.apply(score, reason.points());
            assertThat(score).isBetween(CivicScorePolicy.MIN_SCORE, CivicScorePolicy.MAX_SCORE);
        }
    }

    @Property
    void scoreStaysWithinBoundsForArbitraryDeltas(
            @ForAll @IntRange(min = -5_000, max = 5_000) int start,
            @ForAll @Size(max = 200) List<@IntRange(min = -100_000, max = 100_000) Integer> deltas) {
        int score = CivicScorePolicy.clamp(start);
        for (int d : deltas) {
            score = CivicScorePolicy.apply(score, d);
            assertThat(score).isBetween(0, 900);
        }
    }

    @Property
    void cleaningNeverLowersTheCleanersScore(@ForAll @IntRange(min = 0, max = 900) int score) {
        assertThat(CivicScorePolicy.apply(score, ScoreReason.CLEANED_SPOT.points())).isGreaterThanOrEqualTo(score);
    }

    @Property
    void rewardPointsAreNeverNegative(@ForAll @IntRange(min = -1_000, max = 2_000) int score) {
        assertThat(CivicScorePolicy.rewardPoints(score)).isBetween(0, 30);
    }

    @Example
    void ratingBands() {
        assertThat(CivicScorePolicy.rating(900)).isEqualTo(Rating.GHOST_FREE);
        assertThat(CivicScorePolicy.rating(750)).isEqualTo(Rating.GHOST_FREE);
        assertThat(CivicScorePolicy.rating(749)).isEqualTo(Rating.CALM_SPIRITS);
        assertThat(CivicScorePolicy.rating(600)).isEqualTo(Rating.CALM_SPIRITS);
        assertThat(CivicScorePolicy.rating(599)).isEqualTo(Rating.HAUNTED);
        assertThat(CivicScorePolicy.rating(400)).isEqualTo(Rating.HAUNTED);
        assertThat(CivicScorePolicy.rating(399)).isEqualTo(Rating.CURSED);
        assertThat(CivicScorePolicy.rewardPoints(745)).isEqualTo(14);
        assertThat(CivicScorePolicy.rewardPoints(600)).isZero();
    }
}
