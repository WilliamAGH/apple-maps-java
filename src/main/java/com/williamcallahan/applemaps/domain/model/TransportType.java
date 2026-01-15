package com.williamcallahan.applemaps.domain.model;

/**
 * Supported transportation modes for routing and ETA requests.
 */
public enum TransportType {
    AUTOMOBILE("Automobile"),
    TRANSIT("Transit"),
    WALKING("Walking"),
    CYCLING("Cycling");

    private final String apiValue;

    TransportType(String apiValue) {
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
