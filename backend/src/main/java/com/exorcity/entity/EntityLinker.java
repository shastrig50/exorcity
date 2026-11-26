package com.exorcity.entity;

import com.exorcity.common.GeoUtils;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

/** Pure rule: a spot is owned by the nearest entity within {@value #LINK_RADIUS_METERS} m, if any. */
public final class EntityLinker {

    public static final double LINK_RADIUS_METERS = 200d;

    private EntityLinker() {
    }

    public static Optional<ResponsibleEntity> nearest(Collection<ResponsibleEntity> entities, double lat, double lng) {
        return entities.stream()
                .min(Comparator.comparingDouble(e -> distance(e, lat, lng)));
    }

    public static Optional<ResponsibleEntity> nearestWithinRadius(Collection<ResponsibleEntity> entities,
                                                                  double lat, double lng) {
        return nearest(entities, lat, lng)
                .filter(e -> distance(e, lat, lng) <= LINK_RADIUS_METERS);
    }

    public static double distance(ResponsibleEntity e, double lat, double lng) {
        return GeoUtils.distanceMeters(e.getLatitude(), e.getLongitude(), lat, lng);
    }
}
