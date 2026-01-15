package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * A list of place results and lookup errors.
 */
public record PlacesResponse(List<Place> results, List<PlaceLookupError> errors) {
    public PlacesResponse {
        results = normalizeList(results);
        errors = normalizeList(errors);
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
