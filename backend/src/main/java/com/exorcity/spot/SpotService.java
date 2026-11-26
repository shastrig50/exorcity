package com.exorcity.spot;

import com.exorcity.common.PageResponse;
import com.exorcity.common.exception.ConflictException;
import com.exorcity.common.exception.NotFoundException;
import com.exorcity.entity.EntityLinker;
import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.score.CivicScoreService;
import com.exorcity.score.ScoreReason;
import com.exorcity.spot.SpotDtos.CleanSpotRequest;
import com.exorcity.spot.SpotDtos.CreateSpotRequest;
import com.exorcity.spot.SpotDtos.SpotActionResponse;
import com.exorcity.spot.SpotDtos.SpotResponse;
import com.exorcity.spot.SpotDtos.SpotStats;
import com.exorcity.user.Role;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpotService {

    public static final Duration FAST_CLEAN_WINDOW = Duration.ofDays(3);

    private final GarbageSpotRepository spots;
    private final ConfirmationRepository confirmations;
    private final UserRepository users;
    private final ResponsibleEntityRepository entities;
    private final CivicScoreService scores;
    private final CurseService curseService;
    private final SpotMapper mapper;
    private final Clock clock;

    @Transactional(readOnly = true)
    public PageResponse<SpotResponse> list(SpotStatus status, String ward, String bbox, Pageable pageable, Long viewerId) {
        Page<GarbageSpot> page = spots.findAll(SpotSpecifications.filter(status, ward, bbox), pageable);
        return PageResponse.of(page, mapper.toResponses(page.getContent(), viewerId));
    }

    @Transactional(readOnly = true)
    public SpotResponse get(Long id, Long viewerId) {
        return mapper.toResponse(find(id), viewerId);
    }

    @Transactional(readOnly = true)
    public SpotStats stats() {
        return new SpotStats(spots.countByStatus(SpotStatus.OPEN),
                spots.countByStatusAndCursedTrue(SpotStatus.OPEN),
                spots.countByStatus(SpotStatus.CLEANED));
    }

    /** Report a new spot: link to the nearest entity within 200 m, then check for a cursed cluster. */
    @Transactional
    public SpotResponse create(Long reporterId, CreateSpotRequest req) {
        Instant now = Instant.now(clock);
        List<ResponsibleEntity> allEntities = entities.findAll();
        Optional<ResponsibleEntity> linked =
                EntityLinker.nearestWithinRadius(allEntities, req.latitude(), req.longitude());

        String ward = req.ward() != null && !req.ward().isBlank() ? req.ward().trim()
                // Wards are labels in the MVP: fall back to the ward of the closest known entity.
                : EntityLinker.nearest(allEntities, req.latitude(), req.longitude())
                        .map(ResponsibleEntity::getWard).orElse(null);

        GarbageSpot spot = spots.save(GarbageSpot.builder()
                .type(req.type())
                .description(req.description() == null ? null : req.description().trim())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .beforePhotoUrl(req.beforePhotoUrl().trim())
                .status(SpotStatus.OPEN)
                .reportedBy(reporterId)
                .linkedEntityId(linked.map(ResponsibleEntity::getId).orElse(null))
                .ward(ward)
                .createdAt(now)
                .build());

        curseService.evaluate(spot, now);
        return mapper.toResponse(spot, reporterId);
    }

    @Transactional
    public SpotActionResponse confirm(Long spotId, Long userId) {
        GarbageSpot spot = findOpen(spotId);
        if (Objects.equals(spot.getReportedBy(), userId)) {
            throw new ConflictException("You cannot confirm your own report");
        }
        if (confirmations.existsBySpotIdAndUserId(spotId, userId)) {
            throw new ConflictException("You already confirmed this spot");
        }
        Instant now = Instant.now(clock);
        confirmations.save(Confirmation.builder().spotId(spotId).userId(userId).createdAt(now).build());
        spot.setConfirmationCount(spot.getConfirmationCount() + 1);

        User confirmer = user(userId);
        int earned = scores.applyToUser(confirmer, ScoreReason.CONFIRMED_SPOT, spotId, now);

        if (!spot.isFirstConfirmationRewarded()) {
            users.findById(spot.getReportedBy())
                    .ifPresent(r -> scores.applyToUser(r, ScoreReason.REPORT_FIRST_CONFIRMED, spotId, now));
            spot.setFirstConfirmationRewarded(true);
        }
        spots.save(spot);
        return new SpotActionResponse(mapper.toResponse(spot, userId), earned, confirmer.getCivicScore());
    }

    /** Exorcise a spot. Requires an after photo; the cleaner always gains points, never loses. */
    @Transactional
    public SpotActionResponse clean(Long spotId, Long userId, CleanSpotRequest req) {
        GarbageSpot spot = findOpen(spotId);
        User cleaner = user(userId);
        if (!canClean(cleaner, spot)) {
            throw new AccessDeniedException("You are not allowed to clean this spot");
        }
        Instant now = Instant.now(clock);
        spot.setStatus(SpotStatus.CLEANED);
        spot.setAfterPhotoUrl(req.afterPhotoUrl().trim());
        spot.setCleanedAt(now);
        spot.setCleanedBy(userId);
        spot.setCursed(false);

        int earned = scores.applyToUser(cleaner, ScoreReason.CLEANED_SPOT, spotId, now);

        if (spot.getLinkedEntityId() != null
                && !Duration.between(spot.getCreatedAt(), now).minus(FAST_CLEAN_WINDOW).isPositive()) {
            entities.findById(spot.getLinkedEntityId())
                    .ifPresent(e -> scores.applyToEntity(e, ScoreReason.LINKED_SPOT_CLEANED_FAST, spotId, now));
        }
        spots.save(spot);
        return new SpotActionResponse(mapper.toResponse(spot, userId), earned, cleaner.getCivicScore());
    }

    /** Ward officers reject fake reports in their ward; the reporter loses 30. */
    @Transactional
    public SpotActionResponse reject(Long spotId, Long officerId) {
        GarbageSpot spot = findOpen(spotId);
        User officer = user(officerId);
        if (officer.getRole() != Role.WARD_OFFICER || !sameWard(officer.getWard(), spot.getWard())) {
            throw new AccessDeniedException("Only the ward officer of this ward can reject this report");
        }
        Instant now = Instant.now(clock);
        spot.setStatus(SpotStatus.REJECTED);
        spot.setCursed(false);
        users.findById(spot.getReportedBy())
                .ifPresent(r -> scores.applyToUser(r, ScoreReason.REPORT_REJECTED, spotId, now));
        spots.save(spot);
        return new SpotActionResponse(mapper.toResponse(spot, officerId), 0, officer.getCivicScore());
    }

    static boolean canClean(User user, GarbageSpot spot) {
        Role role = user.getRole();
        if (role == Role.CITIZEN) return true;
        if (role.isEntityAdmin()) return user.getEntityId() != null && user.getEntityId().equals(spot.getLinkedEntityId());
        if (role == Role.WARD_OFFICER) return sameWard(user.getWard(), spot.getWard());
        return false;
    }

    private static boolean sameWard(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private GarbageSpot find(Long id) {
        return spots.findById(id).orElseThrow(() -> new NotFoundException("Spot " + id + " not found"));
    }

    private GarbageSpot findOpen(Long id) {
        GarbageSpot spot = find(id);
        if (!spot.isOpen()) throw new ConflictException("This spot is already " + spot.getStatus().name().toLowerCase());
        return spot;
    }

    private User user(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }
}
