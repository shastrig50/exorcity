package com.exorcity.score;

import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The ONLY place Civic Scores change. Every change is clamped to [0, 900]
 * and recorded as a {@link ScoreEvent} with the delta actually applied.
 */
@Service
@RequiredArgsConstructor
public class CivicScoreService {

    private final UserRepository users;
    private final ResponsibleEntityRepository entities;
    private final ScoreEventRepository events;
    private final Clock clock;

    /** @return the points actually applied (may be smaller than nominal near the bounds). */
    @Transactional
    public int applyToUser(User user, ScoreReason reason, Long spotId) {
        return applyToUser(user, reason, spotId, Instant.now(clock));
    }

    @Transactional
    public int applyToUser(User user, ScoreReason reason, Long spotId, Instant at) {
        int before = user.getCivicScore();
        int after = CivicScorePolicy.apply(before, reason.points());
        user.setCivicScore(after);
        users.save(user);
        events.save(ScoreEvent.builder()
                .userId(user.getId()).spotId(spotId).points(after - before)
                .reason(reason).description(reason.label()).createdAt(at).build());
        return after - before;
    }

    @Transactional
    public int applyToEntity(ResponsibleEntity entity, ScoreReason reason, Long spotId) {
        return applyToEntity(entity, reason, spotId, Instant.now(clock));
    }

    @Transactional
    public int applyToEntity(ResponsibleEntity entity, ScoreReason reason, Long spotId, Instant at) {
        int before = entity.getCivicScore();
        int after = CivicScorePolicy.apply(before, reason.points());
        entity.setCivicScore(after);
        entities.save(entity);
        events.save(ScoreEvent.builder()
                .entityId(entity.getId()).spotId(spotId).points(after - before)
                .reason(reason).description(reason.label()).createdAt(at).build());
        return after - before;
    }
}
