package com.williamcallahan.applemaps.domain.model;

/**
 * Supported transportation modes for routing and ETA requests.
 */
public enum TransportType {
    /** Automobile transport mode. */
    AUTOMOBILE("Automobile"),
    /** Transit transport mode. */
    TRANSIT("Transit"),
    /** Walking transport mode. */
    WALKING("Walking"),
    /** Cycling transport mode. */
    CYCLING("Cycling");

    private final String apiValue;

    TransportType(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Returns the Apple Maps Server API value for this transport type.
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
