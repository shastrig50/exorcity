package com.exorcity.user;

import com.exorcity.common.exception.NotFoundException;
import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.score.CivicScorePolicy;
import com.exorcity.score.Rating;
import com.exorcity.score.ScoreEventRepository;
import com.exorcity.score.ScoreEventResponse;
import com.exorcity.spot.ConfirmationRepository;
import com.exorcity.spot.GarbageSpotRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository users;
    private final ResponsibleEntityRepository entities;
    private final GarbageSpotRepository spots;
    private final ConfirmationRepository confirmations;
    private final ScoreEventRepository events;

    public record MeStats(long reports, long confirmations, long exorcisms) {
    }

    public record MeResponse(Long id, String name, String email, Role role, int civicScore, Rating rating,
                             String ratingLabel, int rewardPoints, Long entityId, String entityName, String ward,
                             MeStats stats, List<ScoreEventResponse> history) {
    }

    @Transactional(readOnly = true)
    public MeResponse me(Long id) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        String entityName = u.getEntityId() == null ? null
                : entities.findById(u.getEntityId()).map(ResponsibleEntity::getName).orElse(null);
        Rating rating = CivicScorePolicy.rating(u.getCivicScore());
        MeStats stats = new MeStats(spots.countByReportedBy(id), confirmations.countByUserId(id),
                spots.countByCleanedBy(id));
        List<ScoreEventResponse> history = events.findTop50ByUserIdOrderByCreatedAtDescIdDesc(id).stream()
                .map(ScoreEventResponse::from).toList();
        return new MeResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCivicScore(), rating,
                rating.label(), CivicScorePolicy.rewardPoints(u.getCivicScore()), u.getEntityId(), entityName,
                u.getWard(), stats, history);
    }
}
