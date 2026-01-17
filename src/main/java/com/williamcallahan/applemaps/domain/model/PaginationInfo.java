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
    /**
     * Canonical constructor that normalizes potentially-null page tokens.
     *
     * @param nextPageToken next page token, if available
     * @param prevPageToken previous page token, if available
     * @param totalPageCount total number of pages
     * @param totalResults total number of results
     */
    public PaginationInfo {
        nextPageToken = normalizeOptional(nextPageToken);
        prevPageToken = normalizeOptional(prevPageToken);
    }

    private static Optional<String> normalizeOptional(Optional<String> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }
}
