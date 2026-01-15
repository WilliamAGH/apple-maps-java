package com.williamcallahan.applemaps.domain.model;

/**
 * Route features that can be avoided during direction requests.
 */
public enum DirectionsAvoid {
    TOLLS("Tolls");

    private final String apiValue;

    DirectionsAvoid(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
