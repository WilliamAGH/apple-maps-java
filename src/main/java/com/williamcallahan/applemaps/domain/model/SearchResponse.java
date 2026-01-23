package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An object that contains the search region and an array of place descriptions.
 */
public record SearchResponse(
    Optional<SearchMapRegion> displayMapRegion,
    Optional<PaginationInfo> paginationInfo,
    List<SearchResponsePlace> results
) {
    /**
     * Canonical constructor that normalizes potentially-null optionals and lists.
     *
     * @param displayMapRegion display map region returned by the API, if available
     * @param paginationInfo pagination information, if available
     * @param results search results returned by the API
     */
    public SearchResponse {
        displayMapRegion = normalizeOptional(displayMapRegion);
        paginationInfo = normalizeOptional(paginationInfo);
        results = normalizeList(results);
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
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
