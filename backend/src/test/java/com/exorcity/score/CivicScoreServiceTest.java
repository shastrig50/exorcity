package com.exorcity.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CivicScoreServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final ResponsibleEntityRepository entities = mock(ResponsibleEntityRepository.class);
    private final ScoreEventRepository events = mock(ScoreEventRepository.class);
    private final CivicScoreService service = new CivicScoreService(users, entities, events,
            Clock.fixed(Instant.parse("2026-11-26T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void recordsTheClampedDeltaNotTheNominalOne() {
        User u = User.builder().id(1L).civicScore(890).build();
        int applied = service.applyToUser(u, ScoreReason.CLEANED_SPOT, 5L);
        assertThat(applied).isEqualTo(10);
        assertThat(u.getCivicScore()).isEqualTo(900);
        ArgumentCaptor<ScoreEvent> captor = ArgumentCaptor.forClass(ScoreEvent.class);
        verify(events).save(captor.capture());
        assertThat(captor.getValue().getPoints()).isEqualTo(10);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getEntityId()).isNull();
    }

    @Test
    void entityPenaltyNeverGoesBelowZero() {
        ResponsibleEntity e = ResponsibleEntity.builder().id(7L).civicScore(30).build();
        int applied = service.applyToEntity(e, ScoreReason.LINKED_SPOT_CURSED, 5L);
        assertThat(applied).isEqualTo(-30);
        assertThat(e.getCivicScore()).isZero();
    }

    @Property
    void cleaningThroughTheServiceNeverLowersTheCleanersScore(@ForAll @IntRange(min = 0, max = 900) int start) {
        User u = User.builder().id(1L).civicScore(start).build();
        service.applyToUser(u, ScoreReason.CLEANED_SPOT, 1L);
        assertThat(u.getCivicScore()).isGreaterThanOrEqualTo(start).isLessThanOrEqualTo(900);
    }
}
