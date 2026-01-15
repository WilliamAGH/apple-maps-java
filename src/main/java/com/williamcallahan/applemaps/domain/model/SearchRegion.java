package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A string that describes a search region by its boundary coordinates.
 */
public record SearchRegion(String coordinateBounds) {
    private static final String COORDINATE_SEPARATOR = ",";

    /**
     * Canonical constructor that validates the coordinate bounds are non-null.
     *
     * @param coordinateBounds formatted coordinate bounds string
     */
    public SearchRegion {
        Objects.requireNonNull(coordinateBounds, "coordinateBounds");
    }

    /**
     * Creates a search region from a bounding box.
     *
     * @param northLatitude northern latitude in decimal degrees
     * @param eastLongitude eastern longitude in decimal degrees
     * @param southLatitude southern latitude in decimal degrees
     * @param westLongitude western longitude in decimal degrees
     * @return a search region
     */
    public static SearchRegion fromBounds(
        double northLatitude,
        double eastLongitude,
        double southLatitude,
        double westLongitude
    ) {
        return new SearchRegion(formatBounds(northLatitude, eastLongitude, southLatitude, westLongitude));
    }

    /**
     * Creates a search region from a {@link SearchMapRegion}.
     *
     * @param searchMapRegion the map region to convert
     * @return a search region
     */
    public static SearchRegion fromSearchMapRegion(SearchMapRegion searchMapRegion) {
        Objects.requireNonNull(searchMapRegion, "searchMapRegion");
        return fromBounds(
            searchMapRegion.northLatitude(),
            searchMapRegion.eastLongitude(),
            searchMapRegion.southLatitude(),
            searchMapRegion.westLongitude()
        );
    }

    /**
     * Converts this region into the format used by query parameters.
     *
     * @return the query string value
     */
    public String toQueryString() {
        return coordinateBounds;
    }

    private static String formatBounds(
        double northLatitude,
        double eastLongitude,
        double southLatitude,
        double westLongitude
    ) {
        return String.join(
            COORDINATE_SEPARATOR,
            List.of(
                Double.toString(northLatitude),
                Double.toString(eastLongitude),
                Double.toString(southLatitude),
                Double.toString(westLongitude)
            )
        );
    }
}
