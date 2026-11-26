package com.exorcity.common;

/** Great-circle distance helpers (Haversine). No PostGIS needed at this scale. */
public final class GeoUtils {

    public static final double EARTH_RADIUS_METERS = 6_371_000d;
    private static final double METERS_PER_DEGREE_LAT = 111_320d;

    private GeoUtils() {
    }

    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    /** Latitude span (degrees) covering the given radius; used for bounding-box prefilters. */
    public static double latDelta(double meters) {
        return meters / METERS_PER_DEGREE_LAT;
    }

    /** Longitude span (degrees) covering the given radius at this latitude. */
    public static double lngDelta(double meters, double atLatitude) {
        double cos = Math.max(0.01, Math.cos(Math.toRadians(atLatitude)));
        return meters / (METERS_PER_DEGREE_LAT * cos);
    }
}
