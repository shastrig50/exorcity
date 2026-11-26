package com.exorcity.score;

/** Every way a Civic Score can change, with its nominal points (see domain.md). */
public enum ScoreReason {
    // Citizen
    REPORT_FIRST_CONFIRMED(10, "Your report got its first confirmation"),
    CONFIRMED_SPOT(5, "Confirmed another hunter's sighting"),
    CLEANED_SPOT(25, "Exorcised a garbage spot"),
    REPORT_REJECTED(-30, "Report rejected as fake by a ward officer"),
    // Entity
    LINKED_SPOT_CLEANED_FAST(20, "Linked spot cleaned within 3 days"),
    LINKED_SPOT_OVERDUE(-15, "Linked spot open longer than 7 days"),
    LINKED_SPOT_CURSED(-50, "Linked spot became cursed");

    private final int points;
    private final String label;

    ScoreReason(int points, String label) {
        this.points = points;
        this.label = label;
    }

    public int points() {
        return points;
    }

    public String label() {
        return label;
    }
}
