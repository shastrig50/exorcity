package com.exorcity.leaderboard;

import com.exorcity.common.PageResponse;
import com.exorcity.entity.EntityType;
import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.leaderboard.LeaderboardDtos.CitizenEntry;
import com.exorcity.leaderboard.LeaderboardDtos.EntityEntry;
import com.exorcity.leaderboard.LeaderboardDtos.WardScore;
import com.exorcity.score.CivicScorePolicy;
import com.exorcity.score.Rating;
import com.exorcity.spot.GarbageSpot;
import com.exorcity.spot.GarbageSpotRepository;
import com.exorcity.spot.MonsterLevelCalculator;
import com.exorcity.spot.SpotStatus;
import com.exorcity.user.Role;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository users;
    private final ResponsibleEntityRepository entities;
    private final GarbageSpotRepository spots;
    private final Clock clock;

    @Transactional(readOnly = true)
    public PageResponse<CitizenEntry> citizens(Pageable pageable) {
        Page<User> page = users.findByRoleOrderByCivicScoreDescIdAsc(Role.CITIZEN, pageable);
        int offset = (int) page.getPageable().getOffset();
        List<User> list = page.getContent();
        List<CitizenEntry> entries = IntStream.range(0, list.size()).mapToObj(i -> {
            User u = list.get(i);
            Rating r = CivicScorePolicy.rating(u.getCivicScore());
            return new CitizenEntry(offset + i + 1, u.getId(), u.getName(), u.getCivicScore(), r, r.label(),
                    CivicScorePolicy.rewardPoints(u.getCivicScore()),
                    spots.countByReportedBy(u.getId()), spots.countByCleanedBy(u.getId()));
        }).toList();
        return PageResponse.of(page, entries);
    }

    @Transactional(readOnly = true)
    public PageResponse<EntityEntry> entities(EntityType type, Pageable pageable) {
        Page<ResponsibleEntity> page = type == null
                ? entities.findAllByOrderByCivicScoreDescIdAsc(pageable)
                : entities.findByTypeOrderByCivicScoreDescIdAsc(type, pageable);
        int offset = (int) page.getPageable().getOffset();
        List<ResponsibleEntity> list = page.getContent();
        List<EntityEntry> entries = IntStream.range(0, list.size()).mapToObj(i -> {
            ResponsibleEntity e = list.get(i);
            Rating r = CivicScorePolicy.rating(e.getCivicScore());
            return new EntityEntry(offset + i + 1, e.getId(), e.getName(), e.getType(), e.getWard(), e.getCivicScore(),
                    r, r.label(), spots.countByLinkedEntityIdAndStatus(e.getId(), SpotStatus.OPEN));
        }).toList();
        return PageResponse.of(page, entries);
    }

    /** hauntedScore = Σ monster levels of open spots + 2 × cursed open spots. Most haunted first. */
    @Transactional(readOnly = true)
    public List<WardScore> wardHauntedScores() {
        Instant now = Instant.now(clock);
        Map<String, List<GarbageSpot>> byWard = new TreeMap<>();
        entities.findAll().forEach(e -> byWard.putIfAbsent(e.getWard(), new ArrayList<>()));
        spots.findByStatus(SpotStatus.OPEN).stream()
                .filter(s -> s.getWard() != null)
                .collect(Collectors.groupingBy(GarbageSpot::getWard))
                .forEach((ward, list) -> byWard.computeIfAbsent(ward, w -> new ArrayList<>()).addAll(list));

        List<WardScore> unranked = byWard.entrySet().stream().map(en -> {
            List<GarbageSpot> open = en.getValue();
            int levels = open.stream().mapToInt(s -> MonsterLevelCalculator.levelOf(s, now)).sum();
            long cursed = open.stream().filter(GarbageSpot::isCursed).count();
            int worst = open.stream().mapToInt(s -> MonsterLevelCalculator.levelOf(s, now)).max().orElse(0);
            return new WardScore(0, en.getKey(), open.size(), cursed, worst, levels + (int) (2 * cursed));
        }).sorted(Comparator.comparingInt(WardScore::hauntedScore).reversed()
                .thenComparing(WardScore::ward)).toList();

        return IntStream.range(0, unranked.size()).mapToObj(i -> {
            WardScore w = unranked.get(i);
            return new WardScore(i + 1, w.ward(), w.openSpots(), w.cursedSpots(), w.worstMonsterLevel(), w.hauntedScore());
        }).toList();
    }
}
