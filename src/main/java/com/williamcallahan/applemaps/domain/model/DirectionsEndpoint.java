package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A directions endpoint specified as an address or coordinate pair.
 */
public record DirectionsEndpoint(String formattedLocation) {
    private static final String COORDINATE_SEPARATOR = ",";

    public DirectionsEndpoint {
        formattedLocation = Objects.requireNonNull(formattedLocation, "formattedLocation");
    }

    public static DirectionsEndpoint fromAddress(String address) {
        return new DirectionsEndpoint(address);
    }

    public static DirectionsEndpoint fromLatitudeLongitude(double latitude, double longitude) {
        return new DirectionsEndpoint(formatCoordinatePair(latitude, longitude));
    }

    public String toQueryString() {
        return formattedLocation;
    }

    private static String formatCoordinatePair(double latitude, double longitude) {
        return Double.toString(latitude) + COORDINATE_SEPARATOR + Double.toString(longitude);
    }
}
