package com.exorcity.spot;

import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.spot.SpotDtos.SpotResponse;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Maps spots to DTOs, loading reporter/cleaner/entity names in bulk (no N+1). */
@Component
@RequiredArgsConstructor
public class SpotMapper {

    private final UserRepository users;
    private final ResponsibleEntityRepository entities;
    private final ConfirmationRepository confirmations;
    private final Clock clock;

    public SpotResponse toResponse(GarbageSpot spot, Long viewerId) {
        return toResponses(List.of(spot), viewerId).get(0);
    }

    public List<SpotResponse> toResponses(Collection<GarbageSpot> spots, Long viewerId) {
        if (spots.isEmpty()) return List.of();
        Instant now = Instant.now(clock);

        Set<Long> userIds = new HashSet<>();
        spots.forEach(s -> {
            userIds.add(s.getReportedBy());
            if (s.getCleanedBy() != null) userIds.add(s.getCleanedBy());
        });
        Map<Long, String> userNames = users.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        Set<Long> entityIds = spots.stream().map(GarbageSpot::getLinkedEntityId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, ResponsibleEntity> entityById = entities.findAllById(entityIds).stream()
                .collect(Collectors.toMap(ResponsibleEntity::getId, Function.identity()));

        Set<Long> confirmedByViewer = viewerId == null ? Set.of()
                : new HashSet<>(confirmations.findSpotIdsConfirmedBy(viewerId,
                        spots.stream().map(GarbageSpot::getId).toList()));

        return spots.stream().map(s -> {
            ResponsibleEntity e = s.getLinkedEntityId() == null ? null : entityById.get(s.getLinkedEntityId());
            return new SpotResponse(
                    s.getId(), s.getType(), s.getType().monsterName(), s.getType().emoji(), s.getDescription(),
                    s.getLatitude(), s.getLongitude(), s.getBeforePhotoUrl(), s.getAfterPhotoUrl(), s.getStatus(),
                    s.getReportedBy(), userNames.get(s.getReportedBy()),
                    s.getCleanedBy(), s.getCleanedBy() == null ? null : userNames.get(s.getCleanedBy()),
                    s.getLinkedEntityId(), e == null ? null : e.getName(),
                    s.getWard(), s.getCreatedAt(), s.getCleanedAt(),
                    MonsterLevelCalculator.ageInDays(s.getCreatedAt(), s.getCleanedAt() != null ? s.getCleanedAt() : now),
                    MonsterLevelCalculator.levelOf(s, now),
                    s.getConfirmationCount(), s.isCursed(), confirmedByViewer.contains(s.getId()));
        }).toList();
    }
}
