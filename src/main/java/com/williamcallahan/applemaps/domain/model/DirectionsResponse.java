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
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static List<List<Location>> normalizeStepPaths(List<List<Location>> rawList) {
        List<List<Location>> normalizedPaths = Objects.requireNonNullElse(rawList, List.<List<Location>>of()).stream()
            .map(path -> List.copyOf(Objects.requireNonNullElse(path, List.<Location>of())))
            .toList();
        return List.copyOf(normalizedPaths);
    }
}
