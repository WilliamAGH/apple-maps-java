package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An object that contains autocomplete information for search suggestions.
 */
public record AutocompleteResult(
    String completionUrl,
    List<String> displayLines,
    Optional<Location> location,
    Optional<StructuredAddress> structuredAddress
) {
    public AutocompleteResult {
        completionUrl = Objects.requireNonNull(completionUrl, "completionUrl");
        displayLines = normalizeList(displayLines);
        location = normalizeOptional(location);
        structuredAddress = normalizeOptional(structuredAddress);
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static List<String> normalizeList(List<String> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
