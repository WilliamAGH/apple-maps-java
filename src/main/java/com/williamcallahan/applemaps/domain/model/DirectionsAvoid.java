package com.williamcallahan.applemaps.domain.model;

/**
 * Route features that can be avoided during direction requests.
 */
public enum DirectionsAvoid {
    /**
     * Avoid toll roads where possible.
     */
    TOLLS("Tolls");

    private final String apiValue;

    DirectionsAvoid(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Returns the Apple Maps Server API value for this avoid option.
     *
     * @return the value used by the API
     */
    public String apiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
