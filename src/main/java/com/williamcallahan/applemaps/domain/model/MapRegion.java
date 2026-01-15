package com.williamcallahan.applemaps.domain.model;

/**
 * An object that describes a map region using its boundary coordinates.
 */
public record MapRegion(
    double northLatitude,
    double eastLongitude,
    double southLatitude,
    double westLongitude
) {
}
