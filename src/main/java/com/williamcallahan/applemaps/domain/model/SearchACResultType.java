package com.williamcallahan.applemaps.domain.model;

/**
 * Enumerated values that indicate the result type for autocomplete requests.
 */
public enum SearchACResultType {
    POI("poi"),
    ADDRESS("address"),
    PHYSICAL_FEATURE("physicalFeature"),
    POINT_OF_INTEREST("pointOfInterest"),
    QUERY("query");

    private final String apiValue;

    SearchACResultType(String apiValue) {
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
