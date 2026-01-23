package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A list of alternate Place ID results and lookup errors.
 */
public record AlternateIdsResponse(List<AlternateIdsEntry> results, List<PlaceLookupError> errors) {
    /**
     * Canonical constructor that normalizes potentially-null lists to immutable lists.
     *
     * @param results alternate ID results
     * @param errors lookup errors
     */
    public AlternateIdsResponse {
        results = normalizeList(results);
        errors = normalizeList(errors);
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
