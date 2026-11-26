package com.exorcity.spot;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;

class MonsterLevelProperties {

    @Property
    void levelIsAlwaysBetweenOneAndFive(@ForAll @LongRange(min = -10, max = 10_000) long ageDays,
                                        @ForAll @IntRange(min = -5, max = 10_000) int confirmations) {
        int level = MonsterLevelCalculator.level(ageDays, confirmations);
        assertThat(level).isBetween(MonsterLevelCalculator.MIN_LEVEL, MonsterLevelCalculator.MAX_LEVEL);
    }

    @Property
    void levelNeverDecreasesAsAgeIncreases(@ForAll @LongRange(min = 0, max = 5_000) long ageDays,
                                           @ForAll @LongRange(min = 0, max = 5_000) long extraDays,
                                           @ForAll @IntRange(min = 0, max = 100) int confirmations) {
        assertThat(MonsterLevelCalculator.level(ageDays + extraDays, confirmations))
                .isGreaterThanOrEqualTo(MonsterLevelCalculator.level(ageDays, confirmations));
    }

    @Property
    void levelNeverDecreasesAsConfirmationsIncrease(@ForAll @LongRange(min = 0, max = 5_000) long ageDays,
                                                    @ForAll @IntRange(min = 0, max = 500) int confirmations,
                                                    @ForAll @IntRange(min = 0, max = 500) int extra) {
        assertThat(MonsterLevelCalculator.level(ageDays, confirmations + extra))
                .isGreaterThanOrEqualTo(MonsterLevelCalculator.level(ageDays, confirmations));
    }

    @Example
    void ageBandsMatchTheSpec() {
        assertThat(MonsterLevelCalculator.baseLevel(0)).isEqualTo(1);
        assertThat(MonsterLevelCalculator.baseLevel(2)).isEqualTo(1);
        assertThat(MonsterLevelCalculator.baseLevel(3)).isEqualTo(2);
        assertThat(MonsterLevelCalculator.baseLevel(7)).isEqualTo(2);
        assertThat(MonsterLevelCalculator.baseLevel(8)).isEqualTo(3);
        assertThat(MonsterLevelCalculator.baseLevel(14)).isEqualTo(3);
        assertThat(MonsterLevelCalculator.baseLevel(15)).isEqualTo(4);
        assertThat(MonsterLevelCalculator.baseLevel(30)).isEqualTo(4);
        assertThat(MonsterLevelCalculator.baseLevel(31)).isEqualTo(5);
    }

    @Example
    void everyFiveConfirmationsAddsALevel() {
        assertThat(MonsterLevelCalculator.level(0, 4)).isEqualTo(1);
        assertThat(MonsterLevelCalculator.level(0, 5)).isEqualTo(2);
        assertThat(MonsterLevelCalculator.level(10, 10)).isEqualTo(5);
        assertThat(MonsterLevelCalculator.level(40, 50)).isEqualTo(5);
    }
}
