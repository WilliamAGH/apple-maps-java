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
    /**
     * Canonical constructor that normalizes potentially-null optionals.
     *
     * @param stepPathIndex index into the step paths array, if available
     * @param distanceMeters step distance in meters, if available
     * @param durationSeconds step duration in seconds, if available
     * @param instructions instructions text, if available
     * @param transportType transport type, if available
     */
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
