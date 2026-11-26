package com.exorcity.spot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.exorcity.common.exception.ConflictException;
import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.score.CivicScoreService;
import com.exorcity.score.ScoreReason;
import com.exorcity.spot.SpotDtos.CleanSpotRequest;
import com.exorcity.user.Role;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SpotServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-26T06:00:00Z");

    @Mock GarbageSpotRepository spots;
    @Mock ConfirmationRepository confirmations;
    @Mock UserRepository users;
    @Mock ResponsibleEntityRepository entities;
    @Mock CivicScoreService scores;
    @Mock CurseService curseService;
    @Mock SpotMapper mapper;

    private SpotService service;
    private final User reporter = User.builder().id(1L).role(Role.CITIZEN).civicScore(600).build();
    private final User citizen = User.builder().id(2L).role(Role.CITIZEN).civicScore(600).build();
    private final ResponsibleEntity society = ResponsibleEntity.builder().id(10L).ward("Koramangala").build();

    @BeforeEach
    void setUp() {
        service = new SpotService(spots, confirmations, users, entities, scores, curseService, mapper,
                Clock.fixed(NOW, ZoneOffset.UTC));
        when(users.findById(1L)).thenReturn(Optional.of(reporter));
        when(users.findById(2L)).thenReturn(Optional.of(citizen));
        when(entities.findById(10L)).thenReturn(Optional.of(society));
    }

    private GarbageSpot openSpot(int ageDays) {
        GarbageSpot s = GarbageSpot.builder().id(100L).type(GarbageType.OVERFLOWING_BIN).status(SpotStatus.OPEN)
                .reportedBy(1L).linkedEntityId(10L).ward("Koramangala")
                .createdAt(NOW.minus(Duration.ofDays(ageDays))).build();
        when(spots.findById(100L)).thenReturn(Optional.of(s));
        return s;
    }

    @Test
    void reporterCannotConfirmOwnSpot() {
        openSpot(1);
        assertThatThrownBy(() -> service.confirm(100L, 1L)).isInstanceOf(ConflictException.class);
    }

    @Test
    void firstConfirmationRewardsConfirmerAndReporterOnce() {
        GarbageSpot s = openSpot(1);
        service.confirm(100L, 2L);
        verify(scores).applyToUser(eq(citizen), eq(ScoreReason.CONFIRMED_SPOT), eq(100L), any());
        verify(scores).applyToUser(eq(reporter), eq(ScoreReason.REPORT_FIRST_CONFIRMED), eq(100L), any());
        assertThat(s.getConfirmationCount()).isEqualTo(1);
        assertThat(s.isFirstConfirmationRewarded()).isTrue();
    }

    @Test
    void duplicateConfirmationIsRejected() {
        openSpot(1);
        when(confirmations.existsBySpotIdAndUserId(100L, 2L)).thenReturn(true);
        assertThatThrownBy(() -> service.confirm(100L, 2L)).isInstanceOf(ConflictException.class);
    }

    @Test
    void fastCleanRewardsCleanerAndEntity() {
        GarbageSpot s = openSpot(2);
        service.clean(100L, 2L, new CleanSpotRequest("https://img.example/after.jpg"));
        assertThat(s.getStatus()).isEqualTo(SpotStatus.CLEANED);
        assertThat(s.getCleanedBy()).isEqualTo(2L);
        verify(scores).applyToUser(eq(citizen), eq(ScoreReason.CLEANED_SPOT), eq(100L), any());
        verify(scores).applyToEntity(eq(society), eq(ScoreReason.LINKED_SPOT_CLEANED_FAST), eq(100L), any());
    }

    @Test
    void slowCleanDoesNotRewardEntity() {
        openSpot(5);
        service.clean(100L, 2L, new CleanSpotRequest("https://img.example/after.jpg"));
        verify(scores, never()).applyToEntity(any(), eq(ScoreReason.LINKED_SPOT_CLEANED_FAST), anyLong(), any());
    }

    @Test
    void cleanedSpotCannotBeCleanedAgain() {
        GarbageSpot s = openSpot(1);
        s.setStatus(SpotStatus.CLEANED);
        assertThatThrownBy(() -> service.clean(100L, 2L, new CleanSpotRequest("https://x.example/a.jpg")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void entityAdminCanOnlyCleanOwnLinkedSpots() {
        openSpot(1);
        User otherAdmin = User.builder().id(3L).role(Role.SHOP_OWNER).entityId(99L).build();
        when(users.findById(3L)).thenReturn(Optional.of(otherAdmin));
        assertThatThrownBy(() -> service.clean(100L, 3L, new CleanSpotRequest("https://x.example/a.jpg")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void wardOfficerRejectsFakeReportAndReporterLoses30() {
        GarbageSpot s = openSpot(1);
        User officer = User.builder().id(4L).role(Role.WARD_OFFICER).ward("koramangala").build();
        when(users.findById(4L)).thenReturn(Optional.of(officer));
        service.reject(100L, 4L);
        assertThat(s.getStatus()).isEqualTo(SpotStatus.REJECTED);
        verify(scores).applyToUser(eq(reporter), eq(ScoreReason.REPORT_REJECTED), eq(100L), any());
    }

    @Test
    void officerFromAnotherWardCannotReject() {
        openSpot(1);
        User officer = User.builder().id(4L).role(Role.WARD_OFFICER).ward("Hebbal").build();
        when(users.findById(4L)).thenReturn(Optional.of(officer));
        assertThatThrownBy(() -> service.reject(100L, 4L)).isInstanceOf(AccessDeniedException.class);
    }
}
