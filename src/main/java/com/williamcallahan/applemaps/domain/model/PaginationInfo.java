package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Information for paginating search results.
 */
public record PaginationInfo(
    Optional<String> nextPageToken,
    Optional<String> prevPageToken,
    long totalPageCount,
    long totalResults
) {
    public PaginationInfo {
        nextPageToken = normalizeOptional(nextPageToken);
        prevPageToken = normalizeOptional(prevPageToken);
    }

    private static Optional<String> normalizeOptional(Optional<String> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }
}
