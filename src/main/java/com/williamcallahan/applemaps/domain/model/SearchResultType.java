package com.williamcallahan.applemaps.domain.model;

/**
 * Enumerated values that indicate the result type for search requests.
 */
public enum SearchResultType {
    /** Point of interest result. */
    POI("poi"),
    /** Address result. */
    ADDRESS("address"),
    /** Physical feature result. */
    PHYSICAL_FEATURE("physicalFeature"),
    /** Point of interest result. */
    POINT_OF_INTEREST("pointOfInterest");

    private final String apiValue;

    SearchResultType(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Returns the Apple Maps Server API value for this result type.
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
