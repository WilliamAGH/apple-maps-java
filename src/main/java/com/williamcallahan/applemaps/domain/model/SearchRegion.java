package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A string that describes a search region by its boundary coordinates.
 */
public record SearchRegion(String coordinateBounds) {
    private static final String COORDINATE_SEPARATOR = ",";

    public SearchRegion {
        Objects.requireNonNull(coordinateBounds, "coordinateBounds");
    }

    public static SearchRegion fromBounds(
        double northLatitude,
        double eastLongitude,
        double southLatitude,
        double westLongitude
    ) {
        return new SearchRegion(formatBounds(northLatitude, eastLongitude, southLatitude, westLongitude));
    }

    public static SearchRegion fromSearchMapRegion(SearchMapRegion searchMapRegion) {
        Objects.requireNonNull(searchMapRegion, "searchMapRegion");
        return fromBounds(
            searchMapRegion.northLatitude(),
            searchMapRegion.eastLongitude(),
            searchMapRegion.southLatitude(),
            searchMapRegion.westLongitude()
        );
    }

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
