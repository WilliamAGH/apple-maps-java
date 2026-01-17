package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Information about an error that occurs while processing a request.
 */
public record ErrorResponse(String message, List<String> details) {
    /**
     * Canonical constructor that normalizes potentially-null lists to immutable lists.
     *
     * @param message error message
     * @param details error details
     */
    public ErrorResponse {
        message = Objects.requireNonNull(message, "message");
        details = List.copyOf(Objects.requireNonNullElse(details, List.of()));
    }
}
