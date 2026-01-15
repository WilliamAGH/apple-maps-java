package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A string that describes a search location in terms of latitude and longitude.
 */
public record SearchLocation(String coordinatePair) {
    private static final String COORDINATE_SEPARATOR = ",";

    public SearchLocation {
        Objects.requireNonNull(coordinatePair, "coordinatePair");
    }

    public static SearchLocation fromLatitudeLongitude(
        double latitude,
        double longitude
    ) {
        return new SearchLocation(formatCoordinatePair(latitude, longitude));
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
