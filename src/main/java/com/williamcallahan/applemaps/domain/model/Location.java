package com.williamcallahan.applemaps.domain.model;

/**
 * An object that describes a location in terms of its latitude and longitude.
 */
public record Location(double latitude, double longitude) {
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final String LATITUDE_LABEL = "latitude";
    private static final String LONGITUDE_LABEL = "longitude";
    private static final String FINITE_MESSAGE_TEMPLATE = "%s must be a finite value.";
    private static final String RANGE_MESSAGE_TEMPLATE = "%s must be between %s and %s.";

    /**
     * Canonical constructor that validates coordinate bounds and finiteness.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     */
    public Location {
        validateLatitudeLongitude(latitude, longitude);
    }

    static void validateLatitudeLongitude(double latitude, double longitude) {
        validateCoordinate(latitude, LATITUDE_LABEL, MIN_LATITUDE, MAX_LATITUDE);
        validateCoordinate(longitude, LONGITUDE_LABEL, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    private static void validateCoordinate(
        double coordinate,
        String coordinateLabel,
        double minimum,
        double maximum
    ) {
        if (!Double.isFinite(coordinate)) {
            throw new IllegalArgumentException(FINITE_MESSAGE_TEMPLATE.formatted(coordinateLabel));
        }
        if (coordinate < minimum || coordinate > maximum) {
            throw new IllegalArgumentException(
                RANGE_MESSAGE_TEMPLATE.formatted(coordinateLabel, minimum, maximum)
            );
        }
    }
}
