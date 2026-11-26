package com.exorcity.spot;

import com.exorcity.common.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** Optional filters for GET /api/spots. */
public final class SpotSpecifications {

    private SpotSpecifications() {
    }

    /**
     * @param bbox "minLng,minLat,maxLng,maxLat" (Leaflet's toBBoxString order) or null
     */
    public static Specification<GarbageSpot> filter(SpotStatus status, String ward, String bbox) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> p = new ArrayList<>();
            if (status != null) p.add(cb.equal(root.get("status"), status));
            if (ward != null && !ward.isBlank()) p.add(cb.equal(cb.lower(root.get("ward")), ward.trim().toLowerCase()));
            if (bbox != null && !bbox.isBlank()) {
                double[] b = parseBbox(bbox);
                p.add(cb.between(root.get("longitude"), b[0], b[2]));
                p.add(cb.between(root.get("latitude"), b[1], b[3]));
            }
            return cb.and(p.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    static double[] parseBbox(String bbox) {
        String[] parts = bbox.split(",");
        if (parts.length != 4) throw new BadRequestException("bbox must be minLng,minLat,maxLng,maxLat");
        try {
            double[] b = new double[4];
            for (int i = 0; i < 4; i++) b[i] = Double.parseDouble(parts[i].trim());
            if (b[0] > b[2] || b[1] > b[3]) throw new BadRequestException("bbox min values must be <= max values");
            return b;
        } catch (NumberFormatException e) {
            throw new BadRequestException("bbox must contain four numbers");
        }
    }
}
