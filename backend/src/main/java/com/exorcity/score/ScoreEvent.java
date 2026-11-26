package com.exorcity.score;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Audit trail entry; exactly one of userId / entityId is set. */
@Entity
@Table(name = "score_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long entityId;

    private Long spotId;

    /** The delta actually applied after clamping. */
    private int points;

    @Enumerated(EnumType.STRING)
    private ScoreReason reason;

    private String description;

    private Instant createdAt;
}
