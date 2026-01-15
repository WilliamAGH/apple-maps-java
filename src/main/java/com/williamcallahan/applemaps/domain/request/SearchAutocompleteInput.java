package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.AddressCategory;
import com.williamcallahan.applemaps.domain.model.PoiCategory;
import com.williamcallahan.applemaps.domain.model.SearchACResultType;
import com.williamcallahan.applemaps.domain.model.SearchLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.SearchRegionPriority;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Input parameters for the search autocomplete API.
 */
public record SearchAutocompleteInput(
    String q,
    List<PoiCategory> excludePoiCategories,
    List<PoiCategory> includePoiCategories,
    List<String> limitToCountries,
    List<SearchACResultType> resultTypeFilter,
    List<AddressCategory> includeAddressCategories,
    List<AddressCategory> excludeAddressCategories,
    Optional<String> language,
    Optional<SearchLocation> searchLocation,
    Optional<SearchRegion> searchRegion,
    Optional<UserLocation> userLocation,
    Optional<SearchRegionPriority> searchRegionPriority
) {
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String LIST_SEPARATOR = ",";
    private static final String PARAMETER_QUERY = "q";
    private static final String PARAMETER_EXCLUDE_POI = "excludePoiCategories";
    private static final String PARAMETER_INCLUDE_POI = "includePoiCategories";
    private static final String PARAMETER_LIMIT_TO_COUNTRIES = "limitToCountries";
    private static final String PARAMETER_RESULT_TYPE_FILTER = "resultTypeFilter";
    private static final String PARAMETER_LANGUAGE = "lang";
    private static final String PARAMETER_SEARCH_LOCATION = "searchLocation";
    private static final String PARAMETER_SEARCH_REGION = "searchRegion";
    private static final String PARAMETER_USER_LOCATION = "userLocation";
    private static final String PARAMETER_SEARCH_REGION_PRIORITY = "searchRegionPriority";
    private static final String PARAMETER_INCLUDE_ADDRESS_CATEGORIES = "includeAddressCategories";
    private static final String PARAMETER_EXCLUDE_ADDRESS_CATEGORIES = "excludeAddressCategories";

    /**
     * Canonical constructor that validates required fields and normalizes optional values.
     *
     * @param q query string
     * @param excludePoiCategories POI categories to exclude
     * @param includePoiCategories POI categories to include
     * @param limitToCountries country codes to restrict results to
     * @param resultTypeFilter autocomplete result type filter
     * @param includeAddressCategories address categories to include
     * @param excludeAddressCategories address categories to exclude
     * @param language optional response language (BCP 47)
     * @param searchLocation optional search location hint
     * @param searchRegion optional search region hint
     * @param userLocation optional user location hint
     * @param searchRegionPriority optional search region priority
     */
    public SearchAutocompleteInput {
        q = Objects.requireNonNull(q, "q");
        excludePoiCategories = normalizeList(excludePoiCategories);
        includePoiCategories = normalizeList(includePoiCategories);
        limitToCountries = normalizeList(limitToCountries);
        resultTypeFilter = normalizeList(resultTypeFilter);
        includeAddressCategories = normalizeList(includeAddressCategories);
        excludeAddressCategories = normalizeList(excludeAddressCategories);
        language = normalizeOptional(language);
        searchLocation = normalizeOptional(searchLocation);
        searchRegion = normalizeOptional(searchRegion);
        userLocation = normalizeOptional(userLocation);
        searchRegionPriority = normalizeOptional(searchRegionPriority);
    }

    /**
     * Converts this input to a query string suitable for the Apple Maps Server API.
     *
     * @return a query string beginning with {@code ?}
     */
    public String toQueryString() {
        List<String> parameters = new ArrayList<>();
        parameters.add(formatParameter(PARAMETER_QUERY, encode(q)));
        if (!excludePoiCategories.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_EXCLUDE_POI, joinApiValues(excludePoiCategories, PoiCategory::apiValue)));
        }
        if (!includePoiCategories.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_INCLUDE_POI, joinApiValues(includePoiCategories, PoiCategory::apiValue)));
        }
        if (!limitToCountries.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_LIMIT_TO_COUNTRIES, joinEncoded(limitToCountries)));
        }
        if (!resultTypeFilter.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_RESULT_TYPE_FILTER, joinApiValues(resultTypeFilter, SearchACResultType::apiValue)));
        }
        if (!includeAddressCategories.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_INCLUDE_ADDRESS_CATEGORIES, joinApiValues(includeAddressCategories, AddressCategory::apiValue)));
        }
        if (!excludeAddressCategories.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_EXCLUDE_ADDRESS_CATEGORIES, joinApiValues(excludeAddressCategories, AddressCategory::apiValue)));
        }
        language.ifPresent(value -> parameters.add(formatParameter(PARAMETER_LANGUAGE, encode(value))));
        searchLocation.ifPresent(value -> parameters.add(formatParameter(PARAMETER_SEARCH_LOCATION, value.toQueryString())));
        searchRegion.ifPresent(value -> parameters.add(formatParameter(PARAMETER_SEARCH_REGION, value.toQueryString())));
        userLocation.ifPresent(value -> parameters.add(formatParameter(PARAMETER_USER_LOCATION, value.toQueryString())));
        searchRegionPriority.ifPresent(value -> parameters.add(formatParameter(PARAMETER_SEARCH_REGION_PRIORITY, value.apiValue())));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

    /**
     * Creates a builder initialized with the required query string.
     *
     * @param q query string
     * @return a builder
     */
    public static Builder builder(String q) {
        return new Builder(q);
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static String joinEncoded(List<String> values) {
        return values.stream()
            .map(SearchAutocompleteInput::encode)
            .reduce((left, right) -> left + LIST_SEPARATOR + right)
            .orElse("");
    }

    private static <T> String joinApiValues(List<T> values, Function<T, String> valueMapper) {
        return values.stream()
            .map(valueMapper)
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
     * Builder for {@link SearchAutocompleteInput}.
     */
    public static final class Builder {
        private final String q;
        private List<PoiCategory> excludePoiCategories = List.of();
        private List<PoiCategory> includePoiCategories = List.of();
        private List<String> limitToCountries = List.of();
        private List<SearchACResultType> resultTypeFilter = List.of();
        private List<AddressCategory> includeAddressCategories = List.of();
        private List<AddressCategory> excludeAddressCategories = List.of();
        private Optional<String> language = Optional.empty();
        private Optional<SearchLocation> searchLocation = Optional.empty();
        private Optional<SearchRegion> searchRegion = Optional.empty();
        private Optional<UserLocation> userLocation = Optional.empty();
        private Optional<SearchRegionPriority> searchRegionPriority = Optional.empty();

        private Builder(String q) {
            this.q = Objects.requireNonNull(q, "q");
        }

        /**
         * Sets POI categories to exclude.
         *
         * @param categories categories to exclude
         * @return this builder
         */
        public Builder excludePoiCategories(List<PoiCategory> categories) {
            this.excludePoiCategories = normalizeList(categories);
            return this;
        }

        /**
         * Sets POI categories to include.
         *
         * @param categories categories to include
         * @return this builder
         */
        public Builder includePoiCategories(List<PoiCategory> categories) {
            this.includePoiCategories = normalizeList(categories);
            return this;
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
         * Sets the autocomplete result type filter.
         *
         * @param resultTypes result types to include
         * @return this builder
         */
        public Builder resultTypeFilter(List<SearchACResultType> resultTypes) {
            this.resultTypeFilter = normalizeList(resultTypes);
            return this;
        }

        /**
         * Sets address categories to include.
         *
         * @param categories address categories to include
         * @return this builder
         */
        public Builder includeAddressCategories(List<AddressCategory> categories) {
            this.includeAddressCategories = normalizeList(categories);
            return this;
        }

        /**
         * Sets address categories to exclude.
         *
         * @param categories address categories to exclude
         * @return this builder
         */
        public Builder excludeAddressCategories(List<AddressCategory> categories) {
            this.excludeAddressCategories = normalizeList(categories);
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
         * Sets the search region priority.
         *
         * @param searchRegionPriority the search region priority, or {@code null} to clear
         * @return this builder
         */
        public Builder searchRegionPriority(SearchRegionPriority searchRegionPriority) {
            this.searchRegionPriority = Optional.ofNullable(searchRegionPriority);
            return this;
        }

        /**
         * Builds a {@link SearchAutocompleteInput}.
         *
         * @return an input instance
         */
        public SearchAutocompleteInput build() {
            return new SearchAutocompleteInput(
                q,
                excludePoiCategories,
                includePoiCategories,
                limitToCountries,
                resultTypeFilter,
                includeAddressCategories,
                excludeAddressCategories,
                language,
                searchLocation,
                searchRegion,
                userLocation,
                searchRegionPriority
            );
        }
    }
}
