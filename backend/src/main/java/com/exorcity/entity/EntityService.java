package com.exorcity.entity;

import com.exorcity.common.PageResponse;
import com.exorcity.common.exception.NotFoundException;
import com.exorcity.entity.EntityDtos.DashboardResponse;
import com.exorcity.entity.EntityDtos.DashboardStats;
import com.exorcity.entity.EntityDtos.EntityResponse;
import com.exorcity.score.CivicScorePolicy;
import com.exorcity.score.OverdueSpotJob;
import com.exorcity.score.Rating;
import com.exorcity.score.ScoreEventRepository;
import com.exorcity.score.ScoreEventResponse;
import com.exorcity.spot.GarbageSpot;
import com.exorcity.spot.GarbageSpotRepository;
import com.exorcity.spot.SpotDtos.SpotResponse;
import com.exorcity.spot.SpotMapper;
import com.exorcity.spot.SpotStatus;
import com.exorcity.user.Role;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final ResponsibleEntityRepository entities;
    private final GarbageSpotRepository spots;
    private final UserRepository users;
    private final ScoreEventRepository events;
    private final SpotMapper spotMapper;
    private final Clock clock;

    @Transactional(readOnly = true)
    public PageResponse<EntityResponse> list(EntityType type, Pageable pageable) {
        Page<ResponsibleEntity> page = type == null
                ? entities.findAllByOrderByCivicScoreDescIdAsc(pageable)
                : entities.findByTypeOrderByCivicScoreDescIdAsc(type, pageable);
        return PageResponse.of(page, page.getContent().stream().map(this::toResponse).toList());
    }

    /** Entity admins see their own entity; ward officers see entities in their ward. */
    @Transactional(readOnly = true)
    public DashboardResponse dashboard(Long entityId, Long viewerId) {
        ResponsibleEntity entity = entities.findById(entityId)
                .orElseThrow(() -> new NotFoundException("Entity " + entityId + " not found"));
        User viewer = users.findById(viewerId).orElseThrow(() -> new NotFoundException("User not found"));
        boolean ownAdmin = viewer.getRole().isEntityAdmin() && entityId.equals(viewer.getEntityId());
        boolean wardOfficer = viewer.getRole() == Role.WARD_OFFICER
                && viewer.getWard() != null && viewer.getWard().equalsIgnoreCase(entity.getWard());
        if (!ownAdmin && !wardOfficer) {
            throw new AccessDeniedException("This dashboard belongs to another entity");
        }

        Instant now = Instant.now(clock);
        List<GarbageSpot> linked = spots.findByLinkedEntityIdOrderByCreatedAtAsc(entityId).stream()
                .filter(s -> s.getStatus() != SpotStatus.REJECTED).toList();
        List<SpotResponse> mapped = spotMapper.toResponses(linked, viewerId).stream()
                .sorted(Comparator.comparing((SpotResponse s) -> s.status() != SpotStatus.OPEN)
                        .thenComparing(SpotResponse::monsterLevel, Comparator.reverseOrder()))
                .toList();

        long open = linked.stream().filter(GarbageSpot::isOpen).count();
        long cleaned = linked.stream().filter(s -> s.getStatus() == SpotStatus.CLEANED).count();
        long cursed = linked.stream().filter(s -> s.isOpen() && s.isCursed()).count();
        long overdue = linked.stream()
                .filter(s -> s.isOpen() && s.getCreatedAt().isBefore(now.minus(OverdueSpotJob.OVERDUE_AFTER))).count();
        int worst = mapped.stream().mapToInt(SpotResponse::monsterLevel).max().orElse(0);

        List<ScoreEventResponse> history = events.findTop50ByEntityIdOrderByCreatedAtDescIdDesc(entityId).stream()
                .map(ScoreEventResponse::from).toList();
        return new DashboardResponse(toResponse(entity), new DashboardStats(open, cleaned, cursed, overdue, worst),
                mapped, history);
    }

    public EntityResponse toResponse(ResponsibleEntity e) {
        Rating rating = CivicScorePolicy.rating(e.getCivicScore());
        return new EntityResponse(e.getId(), e.getName(), e.getType(), e.getLatitude(), e.getLongitude(), e.getWard(),
                e.getCivicScore(), rating, rating.label(), CivicScorePolicy.rewardPoints(e.getCivicScore()),
                spots.countByLinkedEntityIdAndStatus(e.getId(), SpotStatus.OPEN));
    }
}
