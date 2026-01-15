package com.williamcallahan.applemaps.domain.model;

/**
 * An object that describes an area to search by its boundary coordinates.
 */
public record SearchMapRegion(
    double northLatitude,
    double eastLongitude,
    double southLatitude,
    double westLongitude
) {
}
