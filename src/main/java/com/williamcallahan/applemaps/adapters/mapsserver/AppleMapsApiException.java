package com.williamcallahan.applemaps.adapters.mapsserver;

import java.util.Objects;

/**
 * Indicates a non-successful response from the Apple Maps API.
 */
public final class AppleMapsApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public AppleMapsApiException(String operation, int statusCode, String responseBody) {
        super("Apple Maps API request failed for " + operation + " (status " + statusCode + ")");
        this.statusCode = statusCode;
        this.responseBody = Objects.requireNonNullElse(responseBody, "");
    }

    public int statusCode() {
        return statusCode;
    }

    public String responseBody() {
        return responseBody;
    }
}
