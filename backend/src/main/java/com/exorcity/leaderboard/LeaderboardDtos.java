package com.exorcity.leaderboard;

import com.exorcity.entity.EntityType;
import com.exorcity.score.Rating;

public final class LeaderboardDtos {

    private LeaderboardDtos() {
    }

    public record CitizenEntry(int rank, Long id, String name, int civicScore, Rating rating, String ratingLabel,
                               int rewardPoints, long reports, long exorcisms) {
    }

    public record EntityEntry(int rank, Long id, String name, EntityType type, String ward, int civicScore,
                              Rating rating, String ratingLabel, long openSpots) {
    }

    public record WardScore(int rank, String ward, long openSpots, long cursedSpots, int worstMonsterLevel,
                            int hauntedScore) {
    }
}
