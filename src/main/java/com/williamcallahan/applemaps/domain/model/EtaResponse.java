package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Estimated time of arrival results for destinations.
 */
public record EtaResponse(List<EtaEstimate> etas) {
    public EtaResponse {
        etas = normalizeList(etas);
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }
}
