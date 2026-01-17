package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * An error associated with a place lookup request.
 */
public record PlaceLookupError(PlaceLookupErrorCode errorCode, String id) {
    /**
     * Canonical constructor that validates required fields.
     *
     * @param errorCode error code returned by the API
     * @param id place identifier associated with the error
     */
    public PlaceLookupError {
        Objects.requireNonNull(errorCode, "errorCode");
        Objects.requireNonNull(id, "id");
    }
}
