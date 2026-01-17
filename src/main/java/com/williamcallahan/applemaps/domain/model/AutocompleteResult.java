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
    /**
     * Canonical constructor that normalizes potentially-null optionals and lists.
     *
     * @param completionUrl completion URL used to resolve this result
     * @param displayLines display lines suitable for presentation
     * @param location optional location associated with the completion
     * @param structuredAddress optional structured address associated with the completion
     */
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
