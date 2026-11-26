package com.exorcity.score;

import java.time.Instant;

public record ScoreEventResponse(Long id, int points, ScoreReason reason, String description, Long spotId,
                                 Instant createdAt) {

    public static ScoreEventResponse from(ScoreEvent e) {
        return new ScoreEventResponse(e.getId(), e.getPoints(), e.getReason(), e.getDescription(), e.getSpotId(),
                e.getCreatedAt());
    }
}
