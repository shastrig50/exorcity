package com.exorcity.score;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreEventRepository extends JpaRepository<ScoreEvent, Long> {

    List<ScoreEvent> findTop50ByUserIdOrderByCreatedAtDescIdDesc(Long userId);

    List<ScoreEvent> findTop50ByEntityIdOrderByCreatedAtDescIdDesc(Long entityId);
}
