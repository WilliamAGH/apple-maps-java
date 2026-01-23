package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * An object that contains autocomplete results.
 */
public record SearchAutocompleteResponse(List<AutocompleteResult> results) {
    /**
     * Canonical constructor that normalizes potentially-null lists to immutable lists.
     *
     * @param results autocomplete results returned by the API
     */
    public SearchAutocompleteResponse {
        results = normalizeList(results);
    }

    private static List<AutocompleteResult> normalizeList(List<AutocompleteResult> rawList) {
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream()
            .filter(Objects::nonNull)
            .toList();
    }
}
