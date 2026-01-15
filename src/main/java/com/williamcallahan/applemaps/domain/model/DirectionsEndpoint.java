package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * A directions endpoint specified as an address or coordinate pair.
 */
public record DirectionsEndpoint(String formattedLocation) {
    private static final String COORDINATE_SEPARATOR = ",";

    /**
     * Canonical constructor that validates the formatted location is non-null.
     *
     * @param formattedLocation formatted endpoint string (address or coordinate pair)
     */
    public DirectionsEndpoint {
        formattedLocation = Objects.requireNonNull(formattedLocation, "formattedLocation");
    }

    /**
     * Creates an endpoint from a free-form address string.
     *
     * @param address the address text
     * @return a directions endpoint
     */
    public static DirectionsEndpoint fromAddress(String address) {
        return new DirectionsEndpoint(address);
    }

    /**
     * Creates an endpoint from latitude/longitude coordinates.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     * @return a directions endpoint
     */
    public static DirectionsEndpoint fromLatitudeLongitude(double latitude, double longitude) {
        return new DirectionsEndpoint(formatCoordinatePair(latitude, longitude));
    }

    /**
     * Converts this endpoint into the format used by query parameters.
     *
     * @return the query string value
     */
    public String toQueryString() {
        return formattedLocation;
    }

    private static String formatCoordinatePair(double latitude, double longitude) {
        return Double.toString(latitude) + COORDINATE_SEPARATOR + Double.toString(longitude);
    }
}
