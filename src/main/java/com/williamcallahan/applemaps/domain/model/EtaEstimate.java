package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;
import java.util.Optional;

/**
 * ETA details for a destination.
 */
public record EtaEstimate(
    Optional<Location> destination,
    Optional<Long> distanceMeters,
    Optional<Long> expectedTravelTimeSeconds,
    Optional<Long> staticTravelTimeSeconds,
    Optional<TransportType> transportType
) {
    public EtaEstimate {
        destination = normalizeOptional(destination);
        distanceMeters = normalizeOptional(distanceMeters);
        expectedTravelTimeSeconds = normalizeOptional(expectedTravelTimeSeconds);
        staticTravelTimeSeconds = normalizeOptional(staticTravelTimeSeconds);
        transportType = normalizeOptional(transportType);
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }
}
