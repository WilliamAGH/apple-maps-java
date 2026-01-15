package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Information about an error that occurs while processing a request.
 */
public record ErrorResponse(String message, List<String> details) {
    public ErrorResponse {
        message = Objects.requireNonNull(message, "message");
        details = List.copyOf(Objects.requireNonNullElse(details, List.of()));
    }
}
