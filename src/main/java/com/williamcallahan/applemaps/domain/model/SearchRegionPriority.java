package com.williamcallahan.applemaps.domain.model;

/**
 * Indicates the importance of the configured search region.
 */
public enum SearchRegionPriority {
    /** Default priority. */
    DEFAULT("default"),
    /** Required priority. */
    REQUIRED("required");

    private final String apiValue;

    SearchRegionPriority(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Returns the Apple Maps Server API value for this priority.
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
