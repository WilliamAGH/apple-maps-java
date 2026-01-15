package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A string that describes the user's location in terms of latitude and longitude.
 */
public record UserLocation(String coordinatePair) {
    private static final String COORDINATE_SEPARATOR = ",";

    public UserLocation {
        Objects.requireNonNull(coordinatePair, "coordinatePair");
    }

    public static UserLocation fromLatitudeLongitude(
        double latitude,
        double longitude
    ) {
        return new UserLocation(formatCoordinatePair(latitude, longitude));
    }

    public String toQueryString() {
        return coordinatePair;
    }

    private static String formatCoordinatePair(
        double latitude,
        double longitude
    ) {
        return (
            Double.toString(latitude) +
            COORDINATE_SEPARATOR +
            Double.toString(longitude)
        );
    }
}
