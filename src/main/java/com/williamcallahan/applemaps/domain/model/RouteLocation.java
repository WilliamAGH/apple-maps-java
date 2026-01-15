package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A coordinate pair formatted for routing inputs.
 */
public record RouteLocation(String coordinatePair) {
    private static final String COORDINATE_SEPARATOR = ",";

    /**
     * Canonical constructor that validates the coordinate pair is non-null.
     *
     * @param coordinatePair formatted coordinate pair
     */
    public RouteLocation {
        coordinatePair = Objects.requireNonNull(coordinatePair, "coordinatePair");
    }

    /**
     * Creates a route location from latitude/longitude coordinates.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     * @return a route location
     */
    public static RouteLocation fromLatitudeLongitude(double latitude, double longitude) {
        return new RouteLocation(formatCoordinatePair(latitude, longitude));
    }

    /**
     * Converts this location into the format used by query parameters.
     *
     * @return the query string value
     */
    public String toQueryString() {
        return coordinatePair;
    }

    private static String formatCoordinatePair(double latitude, double longitude) {
        return Double.toString(latitude) + COORDINATE_SEPARATOR + Double.toString(longitude);
    }
}
