package com.exorcity.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsibleEntityRepository extends JpaRepository<ResponsibleEntity, Long> {

    Page<ResponsibleEntity> findAllByOrderByCivicScoreDescIdAsc(Pageable pageable);

    Page<ResponsibleEntity> findByTypeOrderByCivicScoreDescIdAsc(EntityType type, Pageable pageable);
}
