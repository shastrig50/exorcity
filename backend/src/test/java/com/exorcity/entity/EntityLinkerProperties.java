package com.exorcity.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.exorcity.common.GeoUtils;
import java.util.List;
import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.From;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Scale;
import net.jqwik.api.constraints.Size;

class EntityLinkerProperties {

    // Bengaluru-ish area so many generated entities land within a few hundred metres of the spot.
    private static final double LAT = 12.97;
    private static final double LNG = 77.59;

    @Property
    void neverLinksToAnEntityMoreThan200mAway(
            @ForAll @Size(max = 30) List<@From("nearbyEntity") ResponsibleEntity> candidates,
            @ForAll @DoubleRange(min = -0.004, max = 0.004) @Scale(6) double dLat,
            @ForAll @DoubleRange(min = -0.004, max = 0.004) @Scale(6) double dLng) {
        double lat = LAT + dLat;
        double lng = LNG + dLng;
        Optional<ResponsibleEntity> linked = EntityLinker.nearestWithinRadius(candidates, lat, lng);
        linked.ifPresent(e -> assertThat(GeoUtils.distanceMeters(e.getLatitude(), e.getLongitude(), lat, lng))
                .isLessThanOrEqualTo(EntityLinker.LINK_RADIUS_METERS));
    }

    @Property
    void linksToTheNearestEntityWhenOneIsInRange(
            @ForAll @Size(min = 1, max = 30) List<@From("nearbyEntity") ResponsibleEntity> candidates) {
        Optional<ResponsibleEntity> linked = EntityLinker.nearestWithinRadius(candidates, LAT, LNG);
        double best = candidates.stream().mapToDouble(e -> EntityLinker.distance(e, LAT, LNG)).min().orElseThrow();
        if (best <= EntityLinker.LINK_RADIUS_METERS) {
            assertThat(linked).isPresent();
            assertThat(EntityLinker.distance(linked.get(), LAT, LNG)).isEqualTo(best);
        } else {
            assertThat(linked).isEmpty();
        }
    }

    @Provide
    Arbitrary<ResponsibleEntity> nearbyEntity() {
        Arbitrary<Double> dLat = Arbitraries.doubles().between(-0.006, 0.006).ofScale(6);
        Arbitrary<Double> dLng = Arbitraries.doubles().between(-0.006, 0.006).ofScale(6);
        return Combinators.combine(dLat, dLng).as((a, b) -> ResponsibleEntity.builder()
                .name("Generated").type(EntityType.SHOP).latitude(LAT + a).longitude(LNG + b).ward("Test").build());
    }

    @Example
    void noEntitiesMeansNoLink() {
        assertThat(EntityLinker.nearestWithinRadius(List.of(), LAT, LNG)).isEmpty();
    }

    @Example
    void haversineMatchesAKnownDistance() {
        // ~111.2 km per degree of latitude
        assertThat(GeoUtils.distanceMeters(12.0, 77.0, 13.0, 77.0)).isBetween(111_000d, 111_400d);
    }
}
