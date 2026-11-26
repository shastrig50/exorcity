package com.exorcity.spot;

public enum SpotStatus {
    OPEN,
    CLEANED,
    /** Rejected as fake by a ward officer; hidden from the map but kept for audit. */
    REJECTED
}
