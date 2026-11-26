package com.exorcity.spot;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GarbageSpotRepository extends JpaRepository<GarbageSpot, Long>, JpaSpecificationExecutor<GarbageSpot> {

    /** Bounding-box prefilter for cursed-cluster detection (exact distance is checked in Java). */
    List<GarbageSpot> findByStatusAndCreatedAtAfterAndLatitudeBetweenAndLongitudeBetween(
            SpotStatus status, Instant createdAfter, double minLat, double maxLat, double minLng, double maxLng);

    List<GarbageSpot> findByStatusAndLinkedEntityIdIsNotNullAndOverduePenaltyAppliedFalseAndCreatedAtBefore(
            SpotStatus status, Instant createdBefore);

    List<GarbageSpot> findByLinkedEntityIdOrderByCreatedAtAsc(Long entityId);

    List<GarbageSpot> findByStatus(SpotStatus status);

    long countByStatus(SpotStatus status);

    long countByStatusAndCursedTrue(SpotStatus status);

    long countByLinkedEntityIdAndStatus(Long entityId, SpotStatus status);

    long countByReportedBy(Long userId);

    long countByCleanedBy(Long userId);
}
