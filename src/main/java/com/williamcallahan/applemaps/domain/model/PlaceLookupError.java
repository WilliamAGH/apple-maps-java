package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * An error associated with a place lookup request.
 */
public record PlaceLookupError(PlaceLookupErrorCode errorCode, String id) {
    public PlaceLookupError {
        Objects.requireNonNull(errorCode, "errorCode");
        Objects.requireNonNull(id, "id");
    }
}
