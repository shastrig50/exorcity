package com.exorcity.spot;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class SpotDtos {

    /** Absolute http(s) URL or a site-relative path such as /seed/zombie.svg. */
    public static final String PHOTO_URL_PATTERN = "^(https?://|/).+";

    private SpotDtos() {
    }

    public record CreateSpotRequest(
            @NotNull GarbageType type,
            @Size(max = 1000) String description,
            @NotNull @DecimalMin("-90") @DecimalMax("90") Double latitude,
            @NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude,
            @NotBlank @Size(max = 1000) @Pattern(regexp = PHOTO_URL_PATTERN, message = "must be an http(s) URL")
            String beforePhotoUrl,
            @Size(max = 100) String ward) {
    }

    public record CleanSpotRequest(
            @NotBlank @Size(max = 1000) @Pattern(regexp = PHOTO_URL_PATTERN, message = "must be an http(s) URL")
            String afterPhotoUrl) {
    }

    public record SpotResponse(
            Long id,
            GarbageType type,
            String monsterName,
            String monsterEmoji,
            String description,
            double latitude,
            double longitude,
            String beforePhotoUrl,
            String afterPhotoUrl,
            SpotStatus status,
            Long reportedById,
            String reportedByName,
            Long cleanedById,
            String cleanedByName,
            Long linkedEntityId,
            String linkedEntityName,
            String ward,
            Instant createdAt,
            Instant cleanedAt,
            long ageDays,
            int monsterLevel,
            int confirmationCount,
            boolean cursed,
            boolean confirmedByMe) {
    }

    /** Result of a confirm/clean/reject action, with the caller's applied score delta. */
    public record SpotActionResponse(SpotResponse spot, int pointsEarned, int newCivicScore) {
    }

    public record SpotStats(long openMonsters, long cursedMonsters, long exorcised) {
    }
}
