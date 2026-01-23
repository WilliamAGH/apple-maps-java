package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An object that describes a place with spatial and administrative details.
 */
public record Place(
    Optional<String> id,
    List<String> alternateIds,
    String name,
    Location coordinate,
    Optional<MapRegion> displayMapRegion,
    List<String> formattedAddressLines,
    Optional<StructuredAddress> structuredAddress,
    String country,
    String countryCode
) {
    /**
     * Canonical constructor that normalizes potentially-null optionals and lists.
     *
     * @param id place identifier, if available
     * @param alternateIds alternate place identifiers
     * @param name place name
     * @param coordinate place coordinate
     * @param displayMapRegion map region to display, if available
     * @param formattedAddressLines formatted address lines
     * @param structuredAddress structured address, if available
     * @param country country name
     * @param countryCode country code
     */
    public Place {
        id = normalizeOptional(id);
        alternateIds = normalizeList(alternateIds);
        displayMapRegion = normalizeOptional(displayMapRegion);
        formattedAddressLines = normalizeList(formattedAddressLines);
        structuredAddress = normalizeOptional(structuredAddress);
        name = Objects.requireNonNull(name, "name");
        coordinate = Objects.requireNonNull(coordinate, "coordinate");
        country = Objects.requireNonNull(country, "country");
        countryCode = Objects.requireNonNull(countryCode, "countryCode");
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream()
            .filter(Objects::nonNull)
            .toList();
    }
}
