package com.williamcallahan.applemaps.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocationValidationTest {
    private static final double VALID_LATITUDE = 37.7;
    private static final double VALID_LONGITUDE = -122.4;
    private static final double INVALID_LATITUDE_TOO_HIGH = 90.1;
    private static final double INVALID_LONGITUDE_TOO_LOW = -180.1;
    private static final double NOT_A_NUMBER = Double.NaN;
    private static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;

    @Test
    void acceptsValidCoordinates() {
        assertDoesNotThrow(() -> new Location(VALID_LATITUDE, VALID_LONGITUDE));
    }

    @Test
    void rejectsNaNLatitude() {
        assertThrows(IllegalArgumentException.class, () -> new Location(NOT_A_NUMBER, VALID_LONGITUDE));
    }

    @Test
    void rejectsInfiniteLongitude() {
        assertThrows(IllegalArgumentException.class, () -> new Location(VALID_LATITUDE, POSITIVE_INFINITY));
    }

    @Test
    void rejectsLatitudeAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> new Location(INVALID_LATITUDE_TOO_HIGH, VALID_LONGITUDE));
    }

    @Test
    void rejectsLongitudeBelowRange() {
        assertThrows(IllegalArgumentException.class, () -> new Location(VALID_LATITUDE, INVALID_LONGITUDE_TOO_LOW));
    }
}
