package com.williamcallahan.applemaps.domain.request;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Input parameters for place lookup requests.
 */
public record PlaceLookupInput(List<String> ids, Optional<String> language) {
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String LIST_SEPARATOR = ",";
    private static final String PARAMETER_IDS = "ids";
    private static final String PARAMETER_LANGUAGE = "lang";

    /**
     * Canonical constructor that validates required fields and normalizes optional values.
     *
     * @param ids place identifiers to look up
     * @param language optional response language (BCP 47)
     */
    public PlaceLookupInput {
        ids = normalizeList(ids);
        language = normalizeOptional(language);
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
        language.ifPresent(languageTag -> parameters.add(formatParameter(PARAMETER_LANGUAGE, encode(languageTag))));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

    /**
     * Creates a builder initialized with the required place IDs.
     *
     * @param ids place identifiers to look up
     * @return a builder
     */
    public static Builder builder(List<String> ids) {
        return new Builder(ids);
    }

    private static void validateIds(List<String> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("PlaceLookupInput ids cannot be empty.");
        }
    }

    private static List<String> normalizeList(List<String> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static String joinEncoded(List<String> values) {
        return values.stream()
            .map(PlaceLookupInput::encode)
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
     * Builder for {@link PlaceLookupInput}.
     */
    public static final class Builder {
        private final List<String> ids;
        private Optional<String> language = Optional.empty();

        private Builder(List<String> ids) {
            this.ids = normalizeList(ids);
        }

        /**
         * Sets the response language (BCP 47).
         *
         * @param language the language tag, or {@code null} to clear
         * @return this builder
         */
        public Builder language(String language) {
            this.language = Optional.ofNullable(language);
            return this;
        }

        /**
         * Builds a validated {@link PlaceLookupInput}.
         *
         * @return an input instance
         */
        public PlaceLookupInput build() {
            return new PlaceLookupInput(ids, language);
        }
    }
}
