package com.exorcity.entity;

import com.exorcity.score.CivicScorePolicy;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A housing society, corporate campus or shop responsible for nearby garbage spots. */
@Entity
@Table(name = "responsible_entities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsibleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private EntityType type;

    private double latitude;

    private double longitude;

    private String ward;

    @Builder.Default
    private int civicScore = CivicScorePolicy.DEFAULT_SCORE;
}
