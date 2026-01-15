package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;
import java.util.Optional;

/**
 * A step along a directions route.
 */
public record DirectionsStep(
    Optional<Integer> stepPathIndex,
    Optional<Long> distanceMeters,
    Optional<Long> durationSeconds,
    Optional<String> instructions,
    Optional<TransportType> transportType
) {
    public DirectionsStep {
        stepPathIndex = normalizeOptional(stepPathIndex);
        distanceMeters = normalizeOptional(distanceMeters);
        durationSeconds = normalizeOptional(durationSeconds);
        instructions = normalizeOptional(instructions);
        transportType = normalizeOptional(transportType);
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }
}
