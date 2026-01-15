package com.williamcallahan.applemaps.domain.model;

/**
 * Indicates the importance of the configured search region.
 */
public enum SearchRegionPriority {
    DEFAULT("default"),
    REQUIRED("required");

    private final String apiValue;

    SearchRegionPriority(String apiValue) {
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
