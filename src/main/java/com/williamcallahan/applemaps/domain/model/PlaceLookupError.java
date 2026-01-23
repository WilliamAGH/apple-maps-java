package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error associated with a place lookup request.
 */
public record PlaceLookupError(
    PlaceLookupErrorCode errorCode,
    @JsonProperty("id") String rawId
) {
    /**
     * Canonical constructor that validates required fields.
     *
     * @param errorCode error code returned by the API
     * @param rawId place identifier associated with the error, may be null
     */
    public PlaceLookupError {
        Objects.requireNonNull(errorCode, "errorCode");
    }

    /**
     * Returns the place identifier associated with this error, if available.
     *
     * @return the place identifier, or empty if not associated with a specific place
     */
    public Optional<String> id() {
        return Optional.ofNullable(rawId);
    }
}
