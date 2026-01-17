package com.williamcallahan.applemaps.adapters.mapsserver;

import java.util.Objects;

/**
 * Indicates a non-successful response from the Apple Maps API.
 */
public final class AppleMapsApiException extends RuntimeException {
    /**
     * HTTP status code returned by the Apple Maps Server API.
     */
    private final int statusCode;
    /**
     * Response body returned by the Apple Maps Server API (may be empty).
     */
    private final String responseBody;

    /**
     * Creates an exception for a non-successful Apple Maps Server API response.
     *
     * @param operation a short operation name (for example, {@code "search"})
     * @param statusCode the HTTP status code
     * @param responseBody the response body, if available
     */
    public AppleMapsApiException(String operation, int statusCode, String responseBody) {
        super("Apple Maps API request failed for " + operation + " (status " + statusCode + ")");
        this.statusCode = statusCode;
        this.responseBody = Objects.requireNonNullElse(responseBody, "");
    }

    /**
     * Returns the HTTP status code from the API response.
     *
     * @return the status code
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * Returns the API response body (may be empty).
     *
     * @return the response body
     */
    public String responseBody() {
        return responseBody;
    }
}
