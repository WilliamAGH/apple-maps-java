package com.williamcallahan.applemaps.domain.request;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Input parameters for alternate place ID requests.
 */
public record AlternateIdsInput(List<String> ids) {
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String PARAMETER_IDS = "ids";
    private static final String LIST_SEPARATOR = ",";

    /**
     * Canonical constructor that validates required fields and normalizes collections.
     *
     * @param ids place identifiers to resolve alternate IDs for
     */
    public AlternateIdsInput {
        ids = normalizeList(ids);
        validateIds(ids);
    }

    /**
     * Converts this input to a query string suitable for the Apple Maps Server API.
     *
     * @return a query string beginning with {@code ?}
     */
    public String toQueryString() {
        List<String> parameters = new ArrayList<>();
        parameters.add(formatParameter(PARAMETER_IDS, joinEncoded(ids)));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

    /**
     * Creates a builder initialized with the required place IDs.
     *
     * @param ids the place IDs to resolve alternate IDs for
     * @return a builder
     */
    public static Builder builder(List<String> ids) {
        return new Builder(ids);
    }

    private static void validateIds(List<String> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("AlternateIdsInput ids cannot be empty.");
        }
    }

    private static List<String> normalizeList(List<String> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static String joinEncoded(List<String> values) {
        return values.stream()
            .map(AlternateIdsInput::encode)
            .reduce((left, right) -> left + LIST_SEPARATOR + right)
            .orElse("");
    }

    private static String formatParameter(String name, String parameterText) {
        return name + "=" + parameterText;
    }

    private static String encode(String rawText) {
        return URLEncoder.encode(rawText, StandardCharsets.UTF_8);
    }

    /**
     * Builder for {@link AlternateIdsInput}.
     */
    public static final class Builder {
        private final List<String> ids;

        private Builder(List<String> ids) {
            this.ids = normalizeList(ids);
        }

        /**
         * Builds a validated {@link AlternateIdsInput}.
         *
         * @return an input instance
         */
        public AlternateIdsInput build() {
            return new AlternateIdsInput(ids);
        }
    }
}
