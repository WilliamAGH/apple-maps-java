package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;
import java.util.Optional;

/**
 * An error associated with a place lookup request.
 */
public record PlaceLookupError(PlaceLookupErrorCode errorCode, Optional<String> id) {
    /**
     * Canonical constructor that validates required fields.
     *
     * @param errorCode error code returned by the API
     * @param id place identifier associated with the error, if available
     */
    public PlaceLookupError {
        Objects.requireNonNull(errorCode, "errorCode");
        id = Objects.requireNonNullElse(id, Optional.empty());
    }
}
