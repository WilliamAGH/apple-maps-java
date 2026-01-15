package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.AddressCategory;
import com.williamcallahan.applemaps.domain.model.PoiCategory;
import com.williamcallahan.applemaps.domain.model.SearchLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.SearchRegionPriority;
import com.williamcallahan.applemaps.domain.model.SearchResultType;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Input parameters for the search API.
 */
public record SearchInput(
    String q,
    List<PoiCategory> excludePoiCategories,
    List<PoiCategory> includePoiCategories,
    List<String> limitToCountries,
    List<SearchResultType> resultTypeFilter,
    List<AddressCategory> includeAddressCategories,
    List<AddressCategory> excludeAddressCategories,
    Optional<String> language,
    Optional<SearchLocation> searchLocation,
    Optional<SearchRegion> searchRegion,
    Optional<UserLocation> userLocation,
    Optional<SearchRegionPriority> searchRegionPriority,
    Optional<Boolean> enablePagination,
    Optional<String> pageToken
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
    private static final String PARAMETER_ENABLE_PAGINATION = "enablePagination";
    private static final String PARAMETER_PAGE_TOKEN = "pageToken";
    private static final String PARAMETER_INCLUDE_ADDRESS_CATEGORIES = "includeAddressCategories";
    private static final String PARAMETER_EXCLUDE_ADDRESS_CATEGORIES = "excludeAddressCategories";

    public SearchInput {
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
        enablePagination = normalizeOptional(enablePagination);
        pageToken = normalizeOptional(pageToken);
    }

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
            parameters.add(formatParameter(PARAMETER_RESULT_TYPE_FILTER, joinApiValues(resultTypeFilter, SearchResultType::apiValue)));
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
        enablePagination.ifPresent(value -> parameters.add(formatParameter(PARAMETER_ENABLE_PAGINATION, value.toString())));
        pageToken.ifPresent(value -> parameters.add(formatParameter(PARAMETER_PAGE_TOKEN, encode(value))));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

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
            .map(SearchInput::encode)
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

    public static final class Builder {
        private final String q;
        private List<PoiCategory> excludePoiCategories = List.of();
        private List<PoiCategory> includePoiCategories = List.of();
        private List<String> limitToCountries = List.of();
        private List<SearchResultType> resultTypeFilter = List.of();
        private List<AddressCategory> includeAddressCategories = List.of();
        private List<AddressCategory> excludeAddressCategories = List.of();
        private Optional<String> language = Optional.empty();
        private Optional<SearchLocation> searchLocation = Optional.empty();
        private Optional<SearchRegion> searchRegion = Optional.empty();
        private Optional<UserLocation> userLocation = Optional.empty();
        private Optional<SearchRegionPriority> searchRegionPriority = Optional.empty();
        private Optional<Boolean> enablePagination = Optional.empty();
        private Optional<String> pageToken = Optional.empty();

        private Builder(String q) {
            this.q = Objects.requireNonNull(q, "q");
        }

        public Builder excludePoiCategories(List<PoiCategory> categories) {
            this.excludePoiCategories = normalizeList(categories);
            return this;
        }

        public Builder includePoiCategories(List<PoiCategory> categories) {
            this.includePoiCategories = normalizeList(categories);
            return this;
        }

        public Builder limitToCountries(List<String> countries) {
            this.limitToCountries = normalizeList(countries);
            return this;
        }

        public Builder resultTypeFilter(List<SearchResultType> resultTypes) {
            this.resultTypeFilter = normalizeList(resultTypes);
            return this;
        }

        public Builder includeAddressCategories(List<AddressCategory> categories) {
            this.includeAddressCategories = normalizeList(categories);
            return this;
        }

        public Builder excludeAddressCategories(List<AddressCategory> categories) {
            this.excludeAddressCategories = normalizeList(categories);
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

        public Builder searchRegionPriority(SearchRegionPriority searchRegionPriority) {
            this.searchRegionPriority = Optional.ofNullable(searchRegionPriority);
            return this;
        }

        public Builder enablePagination(Boolean enablePagination) {
            this.enablePagination = Optional.ofNullable(enablePagination);
            return this;
        }

        public Builder pageToken(String pageToken) {
            this.pageToken = Optional.ofNullable(pageToken);
            return this;
        }

        public SearchInput build() {
            return new SearchInput(
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
                searchRegionPriority,
                enablePagination,
                pageToken
            );
        }
    }
}
