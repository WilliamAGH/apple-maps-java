package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A route returned by the directions service.
 */
public record DirectionsRoute(
    Optional<String> name,
    Optional<Long> distanceMeters,
    Optional<Long> durationSeconds,
    Optional<Boolean> hasTolls,
    List<Integer> stepIndexes,
    Optional<TransportType> transportType
) {
    public DirectionsRoute {
        name = normalizeOptional(name);
        distanceMeters = normalizeOptional(distanceMeters);
        durationSeconds = normalizeOptional(durationSeconds);
        hasTolls = normalizeOptional(hasTolls);
        stepIndexes = normalizeList(stepIndexes);
        transportType = normalizeOptional(transportType);
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
