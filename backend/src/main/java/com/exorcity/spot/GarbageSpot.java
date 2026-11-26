package com.exorcity.spot;

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

@Entity
@Table(name = "garbage_spots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarbageSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GarbageType type;

    private String description;

    private double latitude;

    private double longitude;

    private String beforePhotoUrl;

    private String afterPhotoUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SpotStatus status = SpotStatus.OPEN;

    private Long reportedBy;

    private Long cleanedBy;

    private Long linkedEntityId;

    private String ward;

    private Instant createdAt;

    private Instant cleanedAt;

    private int confirmationCount;

    private boolean cursed;

    private boolean firstConfirmationRewarded;

    private boolean cursePenaltyApplied;

    private boolean overduePenaltyApplied;

    public boolean isOpen() {
        return status == SpotStatus.OPEN;
    }
}
