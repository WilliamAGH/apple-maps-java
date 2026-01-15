package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An object that describes the detailed address components of a place.
 */
public record StructuredAddress(
    Optional<String> administrativeArea,
    Optional<String> administrativeAreaCode,
    Optional<String> subAdministrativeArea,
    List<String> areasOfInterest,
    List<String> dependentLocalities,
    Optional<String> fullThoroughfare,
    Optional<String> locality,
    Optional<String> postCode,
    Optional<String> subLocality,
    Optional<String> subThoroughfare,
    Optional<String> thoroughfare
) {
    public StructuredAddress {
        administrativeArea = normalizeOptional(administrativeArea);
        administrativeAreaCode = normalizeOptional(administrativeAreaCode);
        subAdministrativeArea = normalizeOptional(subAdministrativeArea);
        fullThoroughfare = normalizeOptional(fullThoroughfare);
        locality = normalizeOptional(locality);
        postCode = normalizeOptional(postCode);
        subLocality = normalizeOptional(subLocality);
        subThoroughfare = normalizeOptional(subThoroughfare);
        thoroughfare = normalizeOptional(thoroughfare);
        areasOfInterest = normalizeList(areasOfInterest);
        dependentLocalities = normalizeList(dependentLocalities);
    }

    private static Optional<String> normalizeOptional(Optional<String> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static List<String> normalizeList(List<String> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
