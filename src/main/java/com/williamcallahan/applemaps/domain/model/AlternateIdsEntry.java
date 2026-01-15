package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Alternate Place identifiers associated with a primary Place ID.
 */
public record AlternateIdsEntry(Optional<String> id, List<String> alternateIds) {
    public AlternateIdsEntry {
        id = normalizeOptional(id);
        alternateIds = normalizeList(alternateIds);
    }

    private static Optional<String> normalizeOptional(Optional<String> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static List<String> normalizeList(List<String> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
