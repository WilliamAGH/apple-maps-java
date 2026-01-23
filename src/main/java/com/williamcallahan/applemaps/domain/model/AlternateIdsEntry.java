package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Alternate Place identifiers associated with a primary Place ID.
 */
public record AlternateIdsEntry(Optional<String> id, List<String> alternateIds) {
    /**
     * Canonical constructor that normalizes potentially-null inputs to non-null values.
     *
     * @param id the primary place ID (optional)
     * @param alternateIds alternate place IDs associated with {@code id}
     */
    public AlternateIdsEntry {
        id = normalizeOptional(id);
        alternateIds = normalizeList(alternateIds);
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
