package com.williamcallahan.applemaps.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Estimated time of arrival results for destinations.
 */
public record EtaResponse(List<EtaEstimate> etas) {
    /**
     * Canonical constructor that normalizes potentially-null lists to immutable lists.
     *
     * @param etas ETA estimates returned by the API
     */
    public EtaResponse {
        etas = normalizeList(etas);
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
