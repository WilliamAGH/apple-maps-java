package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * An object that contains an array of places.
 */
public record PlaceResults(List<Place> results) {
    /**
     * Canonical constructor that normalizes potentially-null lists to immutable lists.
     *
     * @param results places returned by the API
     */
    public PlaceResults {
        results = normalizeList(results);
    }

    private static List<Place> normalizeList(List<Place> rawList) {
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream()
            .filter(Objects::nonNull)
            .toList();
    }
}
