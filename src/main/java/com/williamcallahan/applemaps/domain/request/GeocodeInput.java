package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.SearchLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Input parameters for the geocoding API.
 */
public record GeocodeInput(
    String address,
    List<String> limitToCountries,
    Optional<String> language,
    Optional<SearchLocation> searchLocation,
    Optional<SearchRegion> searchRegion,
    Optional<UserLocation> userLocation
) {
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String LIST_SEPARATOR = ",";
    private static final String PARAMETER_QUERY = "q";
    private static final String PARAMETER_LIMIT_TO_COUNTRIES = "limitToCountries";
    private static final String PARAMETER_LANGUAGE = "lang";
    private static final String PARAMETER_SEARCH_LOCATION = "searchLocation";
    private static final String PARAMETER_SEARCH_REGION = "searchRegion";
    private static final String PARAMETER_USER_LOCATION = "userLocation";

    /**
     * Canonical constructor that validates required fields and normalizes optional values.
     *
     * @param address address text to geocode
     * @param limitToCountries country codes to restrict results to
     * @param language optional response language (BCP 47)
     * @param searchLocation optional search location hint
     * @param searchRegion optional search region hint
     * @param userLocation optional user location hint
     */
    public GeocodeInput {
        address = Objects.requireNonNull(address, "address");
        limitToCountries = normalizeList(limitToCountries);
        language = normalizeOptional(language);
        searchLocation = normalizeOptional(searchLocation);
        searchRegion = normalizeOptional(searchRegion);
        userLocation = normalizeOptional(userLocation);
    }

    /**
     * Converts this input to a query string suitable for the Apple Maps Server API.
     *
     * @return a query string beginning with {@code ?}
     */
    public String toQueryString() {
        List<String> parameters = new ArrayList<>();
        parameters.add(formatParameter(PARAMETER_QUERY, encode(address)));
        if (!limitToCountries.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_LIMIT_TO_COUNTRIES, joinEncoded(limitToCountries)));
        }
        language.ifPresent(value -> parameters.add(formatParameter(PARAMETER_LANGUAGE, encode(value))));
        searchLocation.ifPresent(value -> parameters.add(formatParameter(PARAMETER_SEARCH_LOCATION, value.toQueryString())));
        searchRegion.ifPresent(value -> parameters.add(formatParameter(PARAMETER_SEARCH_REGION, value.toQueryString())));
        userLocation.ifPresent(value -> parameters.add(formatParameter(PARAMETER_USER_LOCATION, value.toQueryString())));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

    /**
     * Creates a builder initialized with the required address string.
     *
     * @param address the address text to geocode
     * @return a builder
     */
    public static Builder builder(String address) {
        return new Builder(address);
    }

    private static List<String> normalizeList(List<String> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static String joinEncoded(List<String> values) {
        return values.stream()
            .map(GeocodeInput::encode)
            .reduce((left, right) -> left + LIST_SEPARATOR + right)
            .orElse("");
    }

    private static String formatParameter(String name, String value) {
        return name + "=" + value;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Builder for {@link GeocodeInput}.
     */
    public static final class Builder {
        private final String address;
        private List<String> limitToCountries = List.of();
        private Optional<String> language = Optional.empty();
        private Optional<SearchLocation> searchLocation = Optional.empty();
        private Optional<SearchRegion> searchRegion = Optional.empty();
        private Optional<UserLocation> userLocation = Optional.empty();

        private Builder(String address) {
            this.address = Objects.requireNonNull(address, "address");
        }

        /**
         * Limits results to specific country codes.
         *
         * @param countries country codes (for example, ISO 3166-1 alpha-2)
         * @return this builder
         */
        public Builder limitToCountries(List<String> countries) {
            this.limitToCountries = normalizeList(countries);
            return this;
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
         * Sets the search location hint used by the API.
         *
         * @param searchLocation the search location, or {@code null} to clear
         * @return this builder
         */
        public Builder searchLocation(SearchLocation searchLocation) {
            this.searchLocation = Optional.ofNullable(searchLocation);
            return this;
        }

        /**
         * Sets the search region hint used by the API.
         *
         * @param searchRegion the search region, or {@code null} to clear
         * @return this builder
         */
        public Builder searchRegion(SearchRegion searchRegion) {
            this.searchRegion = Optional.ofNullable(searchRegion);
            return this;
        }

        /**
         * Sets the user location hint used by the API.
         *
         * @param userLocation the user location, or {@code null} to clear
         * @return this builder
         */
        public Builder userLocation(UserLocation userLocation) {
            this.userLocation = Optional.ofNullable(userLocation);
            return this;
        }

        /**
         * Builds a {@link GeocodeInput}.
         *
         * @return an input instance
         */
        public GeocodeInput build() {
            return new GeocodeInput(address, limitToCountries, language, searchLocation, searchRegion, userLocation);
        }
    }
}
