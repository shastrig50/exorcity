package com.exorcity.seed;

import static com.exorcity.spot.GarbageType.ILLEGAL_DUMP;
import static com.exorcity.spot.GarbageType.LAKE_OR_DRAIN;
import static com.exorcity.spot.GarbageType.OVERFLOWING_BIN;
import static com.exorcity.spot.GarbageType.PLASTIC_WASTE;
import static com.exorcity.spot.GarbageType.ROADSIDE_PILE;
import static com.exorcity.spot.SpotStatus.CLEANED;
import static com.exorcity.spot.SpotStatus.OPEN;
import static com.exorcity.spot.SpotStatus.REJECTED;

import com.exorcity.entity.EntityLinker;
import com.exorcity.entity.EntityType;
import com.exorcity.entity.ResponsibleEntity;
import com.exorcity.entity.ResponsibleEntityRepository;
import com.exorcity.score.CivicScoreService;
import com.exorcity.score.OverdueSpotJob;
import com.exorcity.score.ScoreReason;
import com.exorcity.spot.Confirmation;
import com.exorcity.spot.ConfirmationRepository;
import com.exorcity.spot.CurseDetector;
import com.exorcity.spot.GarbageSpot;
import com.exorcity.spot.GarbageSpotRepository;
import com.exorcity.spot.GarbageType;
import com.exorcity.spot.SpotService;
import com.exorcity.spot.SpotStatus;
import com.exorcity.user.Role;
import com.exorcity.user.User;
import com.exorcity.user.UserRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds Bengaluru demo data on an empty database: 6 wards, 8 entities, demo users for every role
 * (password Demo@123) and 43 spots of mixed ages, including one cursed cluster.
 * Scores are produced by replaying the real rules through {@link CivicScoreService}, so the
 * audit trail is consistent. All names are fictional.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "exorcity.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    public static final String DEMO_PASSWORD = "Demo@123";

    private static final String KORAMANGALA = "Koramangala";
    private static final String INDIRANAGAR = "Indiranagar";
    private static final String WHITEFIELD = "Whitefield";
    private static final String JAYANAGAR = "Jayanagar";
    private static final String HEBBAL = "Hebbal";
    private static final String MALLESHWARAM = "Malleshwaram";

    private final UserRepository users;
    private final ResponsibleEntityRepository entities;
    private final GarbageSpotRepository spots;
    private final ConfirmationRepository confirmations;
    private final CivicScoreService scores;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    /**
     * reporter / cleaner = index into the citizens list (cleaner -1 = pick the responsible party);
     * cleanedDaysAgo only used for CLEANED spots.
     */
    private record SeedSpot(GarbageType type, double lat, double lng, String ward, int ageDays, int confirmations,
                            SpotStatus status, int cleanedDaysAgo, int reporter, String description, int cleaner) {
        SeedSpot(GarbageType type, double lat, double lng, String ward, int ageDays, int confirmations,
                 SpotStatus status, int cleanedDaysAgo, int reporter, String description) {
            this(type, lat, lng, ward, ageDays, confirmations, status, cleanedDaysAgo, reporter, description, -1);
        }
    }

    private static final int RAVI = 1;

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() > 0) {
            return;
        }
        Instant now = Instant.now(clock);

        // ---- Entities (fictional) ----
        ResponsibleEntity greenMeadows = entity("Green Meadows Society", EntityType.SOCIETY, 12.9352, 77.6245, KORAMANGALA);
        ResponsibleEntity lakeview = entity("Lakeview Residency", EntityType.SOCIETY, 13.0358, 77.5970, HEBBAL);
        ResponsibleEntity silverOak = entity("Silver Oak Apartments", EntityType.SOCIETY, 12.9250, 77.5938, JAYANAGAR);
        ResponsibleEntity techParkAlpha = entity("Tech Park Alpha", EntityType.CORPORATE_CAMPUS, 12.9698, 77.7500, WHITEFIELD);
        ResponsibleEntity orion = entity("Orion Business Campus", EntityType.CORPORATE_CAMPUS, 12.9784, 77.6408, INDIRANAGAR);
        entity("Nimbus Tech Campus", EntityType.CORPORATE_CAMPUS, 13.0450, 77.5920, HEBBAL);
        ResponsibleEntity kirana = entity("Corner Kirana Store", EntityType.SHOP, 13.0035, 77.5710, MALLESHWARAM);
        entity("Fresh Bites Bakery", EntityType.SHOP, 12.9719, 77.6412, INDIRANAGAR);
        List<ResponsibleEntity> allEntities = entities.findAll();

        // ---- Users ----
        String hash = passwordEncoder.encode(DEMO_PASSWORD);
        List<User> citizens = new ArrayList<>();
        String[][] citizenData = {
                {"Asha Rao", "citizen@exorcity.demo"}, {"Ravi Kumar", "hunter@exorcity.demo"},
                {"Meera Nair", "meera@exorcity.demo"}, {"Kiran Shetty", "kiran@exorcity.demo"},
                {"Arjun Reddy", "arjun@exorcity.demo"}, {"Divya Prakash", "divya@exorcity.demo"},
                {"Farhan Ali", "farhan@exorcity.demo"}, {"Lakshmi Hegde", "lakshmi@exorcity.demo"},
                {"Naveen Bhat", "naveen@exorcity.demo"}, {"Priya Das", "priya@exorcity.demo"}};
        for (String[] c : citizenData) {
            citizens.add(user(c[0], c[1], hash, Role.CITIZEN, null, null, now));
        }
        Map<Long, User> adminByEntity = new HashMap<>();
        adminByEntity.put(greenMeadows.getId(),
                user("Sunita Menon", "society@exorcity.demo", hash, Role.SOCIETY_ADMIN, greenMeadows.getId(), null, now));
        adminByEntity.put(techParkAlpha.getId(),
                user("Vikram Shah", "corporate@exorcity.demo", hash, Role.CORPORATE_ADMIN, techParkAlpha.getId(), null, now));
        adminByEntity.put(kirana.getId(),
                user("Mohan Gowda", "shop@exorcity.demo", hash, Role.SHOP_OWNER, kirana.getId(), null, now));
        adminByEntity.put(lakeview.getId(),
                user("Anil Kapoor", "lakeview@exorcity.demo", hash, Role.SOCIETY_ADMIN, lakeview.getId(), null, now));
        User wardOfficer = user("Officer Rekha Iyer", "ward@exorcity.demo", hash, Role.WARD_OFFICER, null, KORAMANGALA, now);

        // ---- Spots ----
        List<SeedSpot> defs = List.of(
                // Koramangala — cursed cluster behind Green Meadows Society (4 spots on a ~30 m square, all within 50 m)
                new SeedSpot(ILLEGAL_DUMP, 12.93600, 77.62500, KORAMANGALA, 12, 6, OPEN, 0, 0, "Construction debris and household waste dumped behind the society wall"),
                new SeedSpot(ROADSIDE_PILE, 12.93627, 77.62500, KORAMANGALA, 9, 3, OPEN, 0, 1, "Growing pile of mixed waste on the footpath next to the wall"),
                new SeedSpot(PLASTIC_WASTE, 12.93600, 77.62528, KORAMANGALA, 5, 2, OPEN, 0, 2, "Plastic bags and bottles blowing across the lane"),
                new SeedSpot(OVERFLOWING_BIN, 12.93627, 77.62528, KORAMANGALA, 2, 1, OPEN, 0, 3, "Community bin overflowing onto the road"),
                new SeedSpot(ROADSIDE_PILE, 12.93250, 77.62000, KORAMANGALA, 34, 8, OPEN, 0, 4, "Old garbage heap near the bus stop that nobody owns"),
                new SeedSpot(LAKE_OR_DRAIN, 12.93000, 77.62900, KORAMANGALA, 18, 4, OPEN, 0, 5, "Storm drain choked with plastic and food waste"),
                new SeedSpot(OVERFLOWING_BIN, 12.93800, 77.61800, KORAMANGALA, 1, 0, OPEN, 0, 6, "Bin outside the market not emptied today"),
                new SeedSpot(PLASTIC_WASTE, 12.93400, 77.62300, KORAMANGALA, 6, 2, CLEANED, 1, 0, "Snack wrappers piled near the society gate"),
                new SeedSpot(PLASTIC_WASTE, 12.94000, 77.63000, KORAMANGALA, 3, 0, REJECTED, 0, 3, "Test report with an unrelated photo"),
                // Indiranagar
                new SeedSpot(OVERFLOWING_BIN, 12.97220, 77.64180, INDIRANAGAR, 4, 2, OPEN, 0, 0, "Bakery bins overflowing with packaging"),
                new SeedSpot(PLASTIC_WASTE, 12.97900, 77.64000, INDIRANAGAR, 10, 5, OPEN, 0, 7, "Takeaway cups and plastic near the campus back gate"),
                new SeedSpot(ROADSIDE_PILE, 12.96800, 77.63800, INDIRANAGAR, 21, 3, OPEN, 0, 8, "Garbage bags left on the road median"),
                new SeedSpot(ILLEGAL_DUMP, 12.97500, 77.64600, INDIRANAGAR, 45, 8, OPEN, 0, 9, "Old furniture and e-waste dumped on an empty plot"),
                new SeedSpot(LAKE_OR_DRAIN, 12.97000, 77.63500, INDIRANAGAR, 7, 1, OPEN, 0, 1, "Open drain clogged with waste"),
                new SeedSpot(ROADSIDE_PILE, 12.97760, 77.64140, INDIRANAGAR, 2, 1, CLEANED, 0, 2, "Litter near the campus entrance"),
                new SeedSpot(OVERFLOWING_BIN, 12.96500, 77.64500, INDIRANAGAR, 0, 0, OPEN, 0, 4, "Bin overflowing after the weekend"),
                // Whitefield
                new SeedSpot(ILLEGAL_DUMP, 12.97050, 77.75100, WHITEFIELD, 16, 7, OPEN, 0, 5, "Food court waste dumped outside the tech park fence"),
                new SeedSpot(OVERFLOWING_BIN, 12.96900, 77.74900, WHITEFIELD, 3, 2, OPEN, 0, 6, "Bins at the cab pickup point overflowing"),
                new SeedSpot(PLASTIC_WASTE, 12.96500, 77.75500, WHITEFIELD, 26, 4, OPEN, 0, 7, "Plastic waste along the service road"),
                new SeedSpot(LAKE_OR_DRAIN, 12.96000, 77.74500, WHITEFIELD, 40, 8, OPEN, 0, 8, "Lake edge covered in floating trash"),
                new SeedSpot(ROADSIDE_PILE, 12.97500, 77.74200, WHITEFIELD, 0, 0, OPEN, 0, 9, "Fresh pile of bags near the metro station exit"),
                new SeedSpot(ROADSIDE_PILE, 12.97000, 77.74950, WHITEFIELD, 10, 2, CLEANED, 2, 1, "Garbage near the tech park gate"),
                // Jayanagar
                new SeedSpot(OVERFLOWING_BIN, 12.92550, 77.59450, JAYANAGAR, 8, 3, OPEN, 0, 2, "Apartment bin spilling onto the footpath"),
                new SeedSpot(ROADSIDE_PILE, 12.93000, 77.58500, JAYANAGAR, 14, 2, OPEN, 0, 3, "Mixed waste near the shopping complex"),
                new SeedSpot(PLASTIC_WASTE, 12.92000, 77.59800, JAYANAGAR, 2, 0, OPEN, 0, 0, "Plastic sachets scattered near the park"),
                new SeedSpot(LAKE_OR_DRAIN, 12.91800, 77.59000, JAYANAGAR, 31, 6, OPEN, 0, 4, "Drain beside the lake road overflowing with garbage"),
                new SeedSpot(ILLEGAL_DUMP, 12.92300, 77.58800, JAYANAGAR, 20, 3, CLEANED, 3, 5, "Debris dumped near the temple street"),
                new SeedSpot(ROADSIDE_PILE, 12.92450, 77.59300, JAYANAGAR, 1, 1, CLEANED, 0, 6, "Bags outside the apartment gate"),
                // Hebbal
                new SeedSpot(LAKE_OR_DRAIN, 13.03650, 77.59800, HEBBAL, 22, 8, OPEN, 0, 7, "Lake shore littered with plastic near the residency"),
                new SeedSpot(ILLEGAL_DUMP, 13.03000, 77.60000, HEBBAL, 60, 8, OPEN, 0, 8, "Huge dump under the flyover"),
                new SeedSpot(PLASTIC_WASTE, 13.04550, 77.59280, HEBBAL, 6, 2, OPEN, 0, 9, "Plastic packaging behind the campus cafeteria"),
                new SeedSpot(OVERFLOWING_BIN, 13.04000, 77.59000, HEBBAL, 11, 1, OPEN, 0, 1, "Street bin overflowing for over a week"),
                new SeedSpot(ROADSIDE_PILE, 13.03300, 77.59400, HEBBAL, 3, 0, OPEN, 0, 2, "Garbage bags dumped at the junction"),
                new SeedSpot(LAKE_OR_DRAIN, 13.04400, 77.59100, HEBBAL, 4, 1, CLEANED, 1, 3, "Drain near the campus cleared"),
                // Malleshwaram
                new SeedSpot(PLASTIC_WASTE, 13.00400, 77.57150, MALLESHWARAM, 5, 3, OPEN, 0, 4, "Plastic bags piling up beside the kirana store"),
                new SeedSpot(OVERFLOWING_BIN, 13.00300, 77.57020, MALLESHWARAM, 13, 5, OPEN, 0, 5, "Shop-front bin overflowing"),
                new SeedSpot(ROADSIDE_PILE, 13.00800, 77.56500, MALLESHWARAM, 19, 2, OPEN, 0, 6, "Waste heap near the market cross road"),
                new SeedSpot(ILLEGAL_DUMP, 12.99800, 77.57500, MALLESHWARAM, 33, 7, OPEN, 0, 7, "Construction waste blocking the footpath"),
                new SeedSpot(LAKE_OR_DRAIN, 13.01000, 77.57800, MALLESHWARAM, 1, 0, OPEN, 0, 8, "Rainwater drain blocked with waste"),
                new SeedSpot(ROADSIDE_PILE, 13.00200, 77.56900, MALLESHWARAM, 9, 2, CLEANED, 4, 9, "Garbage near the vegetable market"),
                // Ravi's past exorcisms: makes him the top (Ghost Free) hunter
                new SeedSpot(PLASTIC_WASTE, 12.98200, 77.63200, INDIRANAGAR, 25, 0, CLEANED, 20, 4, "Plastic dumped near the park gate", RAVI),
                new SeedSpot(ROADSIDE_PILE, 12.91600, 77.58600, JAYANAGAR, 18, 0, CLEANED, 15, 6, "Bags left at the bus shelter", RAVI),
                new SeedSpot(OVERFLOWING_BIN, 13.03800, 77.60400, HEBBAL, 12, 0, CLEANED, 9, 8, "Overflowing bin at the lake entrance", RAVI));

        List<GarbageSpot> saved = new ArrayList<>();
        for (int i = 0; i < defs.size(); i++) {
            SeedSpot d = defs.get(i);
            Instant createdAt = now.minus(Duration.ofDays(d.ageDays())).minus(Duration.ofHours(1 + i % 5));
            Optional<ResponsibleEntity> linked = EntityLinker.nearestWithinRadius(allEntities, d.lat(), d.lng());
            User reporter = citizens.get(d.reporter());
            GarbageSpot spot = spots.save(GarbageSpot.builder()
                    .type(d.type()).description(d.description()).latitude(d.lat()).longitude(d.lng())
                    .beforePhotoUrl(beforePhoto(d.type())).status(OPEN).reportedBy(reporter.getId())
                    .linkedEntityId(linked.map(ResponsibleEntity::getId).orElse(null))
                    .ward(linked.map(ResponsibleEntity::getWard).orElse(d.ward()))
                    .createdAt(createdAt).build());

            // Confirmations by other citizens (never the reporter; Asha is left free to confirm in demos).
            int made = 0;
            for (int k = 1; k < citizens.size() && made < d.confirmations(); k++) {
                User confirmer = citizens.get((k + i) % citizens.size());
                if (confirmer.getId().equals(reporter.getId()) || confirmer == citizens.get(0)) continue;
                Instant at = createdAt.plus(Duration.ofHours(made + 2L));
                confirmations.save(Confirmation.builder().spotId(spot.getId()).userId(confirmer.getId()).createdAt(at).build());
                scores.applyToUser(confirmer, ScoreReason.CONFIRMED_SPOT, spot.getId(), at);
                if (made == 0) {
                    scores.applyToUser(reporter, ScoreReason.REPORT_FIRST_CONFIRMED, spot.getId(), at);
                    spot.setFirstConfirmationRewarded(true);
                }
                made++;
            }
            spot.setConfirmationCount(made);

            if (d.status() == CLEANED) {
                Instant cleanedAt = now.minus(Duration.ofDays(d.cleanedDaysAgo()));
                User cleaner = d.cleaner() >= 0 ? citizens.get(d.cleaner())
                        : Optional.ofNullable(spot.getLinkedEntityId()).map(adminByEntity::get)
                        .orElseGet(() -> KORAMANGALA.equals(spot.getWard()) ? wardOfficer
                                : citizens.get((d.reporter() + 1) % citizens.size()));
                spot.setStatus(CLEANED);
                spot.setCleanedAt(cleanedAt);
                spot.setCleanedBy(cleaner.getId());
                spot.setAfterPhotoUrl("/seed/exorcised.svg");
                scores.applyToUser(cleaner, ScoreReason.CLEANED_SPOT, spot.getId(), cleanedAt);
                if (spot.getLinkedEntityId() != null
                        && !Duration.between(createdAt, cleanedAt).minus(SpotService.FAST_CLEAN_WINDOW).isPositive()) {
                    entities.findById(spot.getLinkedEntityId()).ifPresent(e ->
                            scores.applyToEntity(e, ScoreReason.LINKED_SPOT_CLEANED_FAST, spot.getId(), cleanedAt));
                }
            } else if (d.status() == REJECTED) {
                spot.setStatus(REJECTED);
                scores.applyToUser(reporter, ScoreReason.REPORT_REJECTED, spot.getId(), createdAt.plus(Duration.ofDays(1)));
            }
            saved.add(spot);
        }

        // ---- Cursed clusters (oldest first so penalties are dated realistically) ----
        List<GarbageSpot> open = saved.stream().filter(GarbageSpot::isOpen)
                .sorted(Comparator.comparing(GarbageSpot::getCreatedAt)).toList();
        for (GarbageSpot s : open) {
            List<GarbageSpot> cluster = CurseDetector.cursedCluster(s, open, now);
            for (GarbageSpot c : cluster) {
                c.setCursed(true);
                if (c.getLinkedEntityId() != null && !c.isCursePenaltyApplied()) {
                    Instant at = cluster.stream().map(GarbageSpot::getCreatedAt).max(Comparator.naturalOrder()).orElse(now);
                    entities.findById(c.getLinkedEntityId())
                            .ifPresent(e -> scores.applyToEntity(e, ScoreReason.LINKED_SPOT_CURSED, c.getId(), at));
                    c.setCursePenaltyApplied(true);
                }
            }
        }

        // ---- Overdue penalties the daily job would already have applied ----
        for (GarbageSpot s : open) {
            if (s.getLinkedEntityId() != null && s.getCreatedAt().isBefore(now.minus(OverdueSpotJob.OVERDUE_AFTER))) {
                Instant at = s.getCreatedAt().plus(OverdueSpotJob.OVERDUE_AFTER).plus(Duration.ofHours(1));
                entities.findById(s.getLinkedEntityId())
                        .ifPresent(e -> scores.applyToEntity(e, ScoreReason.LINKED_SPOT_OVERDUE, s.getId(), at));
                s.setOverduePenaltyApplied(true);
            }
        }
        spots.saveAll(saved);
        log.info("Seeded {} entities, {} users and {} spots (demo password: {})",
                allEntities.size(), users.count(), saved.size(), DEMO_PASSWORD);
    }

    private ResponsibleEntity entity(String name, EntityType type, double lat, double lng, String ward) {
        return entities.save(ResponsibleEntity.builder().name(name).type(type).latitude(lat).longitude(lng).ward(ward).build());
    }

    private User user(String name, String email, String hash, Role role, Long entityId, String ward, Instant now) {
        return users.save(User.builder().name(name).email(email).passwordHash(hash).role(role)
                .entityId(entityId).ward(ward).createdAt(now.minus(Duration.ofDays(70))).build());
    }

    private static String beforePhoto(GarbageType type) {
        return "/seed/" + type.name().toLowerCase().replace('_', '-') + ".svg";
    }
}
