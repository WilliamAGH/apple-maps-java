package com.williamcallahan.applemaps.adapters.mapsserver;

import java.util.Objects;

/**
 * Indicates a client-side failure when calling the Apple Maps API.
 */
public final class AppleMapsClientException extends RuntimeException {
    public AppleMapsClientException(String operation, Throwable cause) {
        super("Apple Maps request failed for " + Objects.requireNonNull(operation, "operation"), cause);
    }
}
