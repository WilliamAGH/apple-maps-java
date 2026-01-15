package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * An object that contains an array of places.
 */
public record PlaceResults(List<Place> results) {
    public PlaceResults {
        results = normalizeList(results);
    }

    private static List<Place> normalizeList(List<Place> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
