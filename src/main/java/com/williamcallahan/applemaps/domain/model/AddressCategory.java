package com.williamcallahan.applemaps.domain.model;

/**
 * Enumerated values that represent political geographical boundaries.
 */
public enum AddressCategory {
    /**
     * Country-level address component.
     */
    COUNTRY("Country"),
    /**
     * Top-level administrative area (for example, a state or province).
     */
    ADMINISTRATIVE_AREA("AdministrativeArea"),
    /**
     * Sub-level administrative area (for example, a county or district).
     */
    SUB_ADMINISTRATIVE_AREA("SubAdministrativeArea"),
    /**
     * Locality address component (for example, a city).
     */
    LOCALITY("Locality"),
    /**
     * Sub-locality address component (for example, a neighborhood).
     */
    SUB_LOCALITY("SubLocality"),
    /**
     * Postal code address component.
     */
    POSTAL_CODE("PostalCode");

    private final String apiValue;

    AddressCategory(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Returns the Apple Maps Server API value for this category.
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
