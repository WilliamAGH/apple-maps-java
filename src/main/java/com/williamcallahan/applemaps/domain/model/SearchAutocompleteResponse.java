package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * An object that contains autocomplete results.
 */
public record SearchAutocompleteResponse(List<AutocompleteResult> results) {
    public SearchAutocompleteResponse {
        results = normalizeList(results);
    }

    private static List<AutocompleteResult> normalizeList(List<AutocompleteResult> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
