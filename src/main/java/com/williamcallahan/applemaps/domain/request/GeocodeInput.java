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

    public GeocodeInput {
        address = Objects.requireNonNull(address, "address");
        limitToCountries = normalizeList(limitToCountries);
        language = normalizeOptional(language);
        searchLocation = normalizeOptional(searchLocation);
        searchRegion = normalizeOptional(searchRegion);
        userLocation = normalizeOptional(userLocation);
    }

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

        public Builder limitToCountries(List<String> countries) {
            this.limitToCountries = normalizeList(countries);
            return this;
        }

        public Builder language(String language) {
            this.language = Optional.ofNullable(language);
            return this;
        }

        public Builder searchLocation(SearchLocation searchLocation) {
            this.searchLocation = Optional.ofNullable(searchLocation);
            return this;
        }

        public Builder searchRegion(SearchRegion searchRegion) {
            this.searchRegion = Optional.ofNullable(searchRegion);
            return this;
        }

        public Builder userLocation(UserLocation userLocation) {
            this.userLocation = Optional.ofNullable(userLocation);
            return this;
        }

        public GeocodeInput build() {
            return new GeocodeInput(address, limitToCountries, language, searchLocation, searchRegion, userLocation);
        }
    }
}
