package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Directions results including routes, steps, and step paths.
 */
public record DirectionsResponse(
    Optional<Place> origin,
    Optional<Place> destination,
    List<DirectionsRoute> routes,
    List<DirectionsStep> steps,
    List<List<Location>> stepPaths
) {
    /**
     * Canonical constructor that normalizes potentially-null optionals and lists.
     *
     * @param origin origin place, if available
     * @param destination destination place, if available
     * @param routes routes returned by the API
     * @param steps steps returned by the API
     * @param stepPaths step path coordinate arrays returned by the API
     */
    public DirectionsResponse {
        origin = normalizeOptional(origin);
        destination = normalizeOptional(destination);
        routes = normalizeList(routes);
        steps = normalizeList(steps);
        stepPaths = normalizeStepPaths(stepPaths);
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

    private static List<List<Location>> normalizeStepPaths(List<List<Location>> rawList) {
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream()
            .map(DirectionsResponse::normalizeStepPath)
            .toList();
    }

    private static List<Location> normalizeStepPath(List<Location> rawPath) {
        if (rawPath == null) {
            return List.of();
        }
        return rawPath.stream()
            .filter(Objects::nonNull)
            .toList();
    }

}
