package com.williamcallahan.applemaps.adapters.mapsserver;

import java.util.Objects;

/**
 * Indicates a client-side failure when calling the Apple Maps API.
 */
public final class AppleMapsClientException extends RuntimeException {
    /**
     * Creates an exception for a client-side failure.
     *
     * @param operation a short operation name (for example, {@code "search"})
     * @param cause the underlying exception
     */
    public AppleMapsClientException(String operation, Throwable cause) {
        super("Apple Maps request failed for " + Objects.requireNonNull(operation, "operation"), cause);
    }
}
