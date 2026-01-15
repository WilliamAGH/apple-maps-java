package com.williamcallahan.applemaps.domain.model;

/**
 * Enumerated values that indicate the result type for search requests.
 */
public enum SearchResultType {
    POI("poi"),
    ADDRESS("address"),
    PHYSICAL_FEATURE("physicalFeature"),
    POINT_OF_INTEREST("pointOfInterest");

    private final String apiValue;

    SearchResultType(String apiValue) {
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
