package com.exorcity.spot;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {

    boolean existsBySpotIdAndUserId(Long spotId, Long userId);

    long countByUserId(Long userId);

    @Query("select c.spotId from Confirmation c where c.userId = :userId and c.spotId in :spotIds")
    List<Long> findSpotIdsConfirmedBy(@Param("userId") Long userId, @Param("spotIds") Collection<Long> spotIds);
}
