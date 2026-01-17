package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A string that describes a search location in terms of latitude and longitude.
 */
public record SearchLocation(String coordinatePair) {
    private static final String COORDINATE_SEPARATOR = ",";

    /**
     * Canonical constructor that validates the coordinate pair is non-null.
     *
     * @param coordinatePair formatted coordinate pair
     */
    public SearchLocation {
        Objects.requireNonNull(coordinatePair, "coordinatePair");
    }

    /**
     * Creates a search location from latitude/longitude coordinates.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     * @return a search location
     */
    public static SearchLocation fromLatitudeLongitude(
        double latitude,
        double longitude
    ) {
        return new SearchLocation(formatCoordinatePair(latitude, longitude));
    }

    /**
     * Converts this location into the format used by query parameters.
     *
     * @return the query string value
     */
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
