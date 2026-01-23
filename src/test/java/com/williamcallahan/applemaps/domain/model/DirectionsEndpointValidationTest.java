package com.williamcallahan.applemaps.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DirectionsEndpointValidationTest {
    private static final double VALID_LATITUDE = 37.7;
    private static final double VALID_LONGITUDE = -122.4;
    private static final double INVALID_LATITUDE_TOO_LOW = -90.1;

    @Test
    void rejectsInvalidLatitude() {
        assertThrows(
            IllegalArgumentException.class,
            () -> DirectionsEndpoint.fromLatitudeLongitude(INVALID_LATITUDE_TOO_LOW, VALID_LONGITUDE)
        );
    }

    @Test
    void acceptsValidCoordinates() {
        assertDoesNotThrow(
            () -> DirectionsEndpoint.fromLatitudeLongitude(VALID_LATITUDE, VALID_LONGITUDE)
        );
    }
}
