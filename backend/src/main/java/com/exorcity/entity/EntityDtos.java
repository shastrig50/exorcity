package com.exorcity.entity;

import com.exorcity.score.Rating;
import com.exorcity.score.ScoreEventResponse;
import com.exorcity.spot.SpotDtos.SpotResponse;
import java.util.List;

public final class EntityDtos {

    private EntityDtos() {
    }

    public record EntityResponse(Long id, String name, EntityType type, double latitude, double longitude,
                                 String ward, int civicScore, Rating rating, String ratingLabel, int rewardPoints,
                                 long openSpots) {
    }

    public record DashboardStats(long openSpots, long cleanedSpots, long cursedSpots, long overdueSpots,
                                 int worstMonsterLevel) {
    }

    public record DashboardResponse(EntityResponse entity, DashboardStats stats, List<SpotResponse> spots,
                                    List<ScoreEventResponse> history) {
    }
}
