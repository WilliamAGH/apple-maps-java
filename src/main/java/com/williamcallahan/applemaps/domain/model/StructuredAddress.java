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
    /**
     * Canonical constructor that normalizes potentially-null optionals and lists.
     *
     * @param administrativeArea administrative area, if available
     * @param administrativeAreaCode administrative area code, if available
     * @param subAdministrativeArea sub-administrative area, if available
     * @param areasOfInterest areas of interest
     * @param dependentLocalities dependent localities
     * @param fullThoroughfare full thoroughfare, if available
     * @param locality locality, if available
     * @param postCode postal code, if available
     * @param subLocality sub-locality, if available
     * @param subThoroughfare sub-thoroughfare, if available
     * @param thoroughfare thoroughfare, if available
     */
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
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream()
            .filter(Objects::nonNull)
            .toList();
    }
}
