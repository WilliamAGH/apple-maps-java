package com.williamcallahan.applemaps.domain.model;

/**
 * Error codes returned when looking up places by identifier.
 */
public enum PlaceLookupErrorCode {
    /**
     * The request provided an invalid place identifier.
     */
    FAILED_INVALID_ID,
    /**
     * The requested place could not be found.
     */
    FAILED_NOT_FOUND,
    /**
     * The lookup failed due to an internal error.
     */
    FAILED_INTERNAL_ERROR
}
