package com.williamcallahan.applemaps.domain.model;

/**
 * Enumerated values that represent political geographical boundaries.
 */
public enum AddressCategory {
    COUNTRY("Country"),
    ADMINISTRATIVE_AREA("AdministrativeArea"),
    SUB_ADMINISTRATIVE_AREA("SubAdministrativeArea"),
    LOCALITY("Locality"),
    SUB_LOCALITY("SubLocality"),
    POSTAL_CODE("PostalCode");

    private final String apiValue;

    AddressCategory(String apiValue) {
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
