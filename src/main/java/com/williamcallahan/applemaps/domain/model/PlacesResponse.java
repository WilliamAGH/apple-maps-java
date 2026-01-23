package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A list of place results and lookup errors.
 */
public record PlacesResponse(List<Place> results, List<PlaceLookupError> errors) {
    /**
     * Canonical constructor that normalizes potentially-null lists to immutable lists.
     *
     * @param results places returned by the API
     * @param errors lookup errors
     */
    public PlacesResponse {
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
