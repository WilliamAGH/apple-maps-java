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
    /**
     * Canonical constructor that normalizes potentially-null optionals and lists.
     *
     * @param name route name, if available
     * @param distanceMeters route distance in meters, if available
     * @param durationSeconds route duration in seconds, if available
     * @param hasTolls whether the route has tolls, if available
     * @param stepIndexes indexes into the directions steps array
     * @param transportType transport type, if available
     */
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
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream()
            .filter(Objects::nonNull)
            .toList();
    }
}
