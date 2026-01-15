package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A coordinate pair formatted for routing inputs.
 */
public record RouteLocation(String coordinatePair) {
    private static final String COORDINATE_SEPARATOR = ",";

    public RouteLocation {
        coordinatePair = Objects.requireNonNull(coordinatePair, "coordinatePair");
    }

    public static RouteLocation fromLatitudeLongitude(double latitude, double longitude) {
        return new RouteLocation(formatCoordinatePair(latitude, longitude));
    }

    public String toQueryString() {
        return coordinatePair;
    }

    private static String formatCoordinatePair(double latitude, double longitude) {
        return Double.toString(latitude) + COORDINATE_SEPARATOR + Double.toString(longitude);
    }
}
