package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.DirectionsAvoid;
import com.williamcallahan.applemaps.domain.model.DirectionsEndpoint;
import com.williamcallahan.applemaps.domain.model.RouteLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.TransportType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Input parameters for directions requests.
 */
public record DirectionsInput(
    DirectionsEndpoint origin,
    DirectionsEndpoint destination,
    Optional<String> arrivalDate,
    List<DirectionsAvoid> avoid,
    Optional<String> departureDate,
    Optional<String> language,
    Optional<Boolean> requestsAlternateRoutes,
    Optional<RouteLocation> searchLocation,
    Optional<SearchRegion> searchRegion,
    Optional<TransportType> transportType,
    Optional<RouteLocation> userLocation
) {
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String LIST_SEPARATOR = ",";
    private static final String PARAMETER_ORIGIN = "origin";
    private static final String PARAMETER_DESTINATION = "destination";
    private static final String PARAMETER_ARRIVAL_DATE = "arrivalDate";
    private static final String PARAMETER_AVOID = "avoid";
    private static final String PARAMETER_DEPARTURE_DATE = "departureDate";
    private static final String PARAMETER_LANGUAGE = "lang";
    private static final String PARAMETER_REQUESTS_ALTERNATE_ROUTES = "requestsAlternateRoutes";
    private static final String PARAMETER_SEARCH_LOCATION = "searchLocation";
    private static final String PARAMETER_SEARCH_REGION = "searchRegion";
    private static final String PARAMETER_TRANSPORT_TYPE = "transportType";
    private static final String PARAMETER_USER_LOCATION = "userLocation";

    /**
     * Canonical constructor that validates required fields and normalizes optional values.
     *
     * @param origin the origin endpoint
     * @param destination the destination endpoint
     * @param arrivalDate optional arrival date/time (format as expected by the API)
     * @param avoid route features to avoid
     * @param departureDate optional departure date/time (format as expected by the API)
     * @param language optional response language (BCP 47)
     * @param requestsAlternateRoutes optional flag to request alternate routes
     * @param searchLocation optional search location hint
     * @param searchRegion optional search region hint
     * @param transportType optional transport type
     * @param userLocation optional user location hint
     */
    public DirectionsInput {
        origin = Objects.requireNonNull(origin, "origin");
        destination = Objects.requireNonNull(destination, "destination");
        arrivalDate = normalizeOptional(arrivalDate);
        avoid = normalizeList(avoid);
        departureDate = normalizeOptional(departureDate);
        language = normalizeOptional(language);
        requestsAlternateRoutes = normalizeOptional(requestsAlternateRoutes);
        searchLocation = normalizeOptional(searchLocation);
        searchRegion = normalizeOptional(searchRegion);
        transportType = normalizeOptional(transportType);
        userLocation = normalizeOptional(userLocation);
        validateDates(arrivalDate, departureDate);
    }

    /**
     * Converts this input to a query string suitable for the Apple Maps Server API.
     *
     * @return a query string beginning with {@code ?}
     */
    public String toQueryString() {
        List<String> parameters = new ArrayList<>();
        parameters.add(formatParameter(PARAMETER_ORIGIN, encode(origin.toQueryString())));
        parameters.add(formatParameter(PARAMETER_DESTINATION, encode(destination.toQueryString())));
        arrivalDate.ifPresent(arrivalDateText -> parameters.add(formatParameter(PARAMETER_ARRIVAL_DATE, encode(arrivalDateText))));
        if (!avoid.isEmpty()) {
            parameters.add(formatParameter(PARAMETER_AVOID, joinApiValues(avoid, DirectionsAvoid::apiValue)));
        }
        departureDate.ifPresent(departureDateText -> parameters.add(formatParameter(PARAMETER_DEPARTURE_DATE, encode(departureDateText))));
        language.ifPresent(languageTag -> parameters.add(formatParameter(PARAMETER_LANGUAGE, encode(languageTag))));
        requestsAlternateRoutes.ifPresent(alternateRoutesFlag ->
            parameters.add(formatParameter(PARAMETER_REQUESTS_ALTERNATE_ROUTES, alternateRoutesFlag.toString())));
        searchLocation.ifPresent(searchLocationInput ->
            parameters.add(formatParameter(PARAMETER_SEARCH_LOCATION, searchLocationInput.toQueryString())));
        searchRegion.ifPresent(searchRegionInput ->
            parameters.add(formatParameter(PARAMETER_SEARCH_REGION, searchRegionInput.toQueryString())));
        transportType.ifPresent(transportMode ->
            parameters.add(formatParameter(PARAMETER_TRANSPORT_TYPE, transportMode.apiValue())));
        userLocation.ifPresent(userLocationInput ->
            parameters.add(formatParameter(PARAMETER_USER_LOCATION, userLocationInput.toQueryString())));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

    /**
     * Creates a builder initialized with the required origin and destination.
     *
     * @param origin the origin endpoint
     * @param destination the destination endpoint
     * @return a builder
     */
    public static Builder builder(DirectionsEndpoint origin, DirectionsEndpoint destination) {
        return new Builder(origin, destination);
    }

    private static void validateDates(Optional<String> arrivalDate, Optional<String> departureDate) {
        if (arrivalDate.isPresent() && departureDate.isPresent()) {
            throw new IllegalArgumentException("Specify arrivalDate or departureDate, not both.");
        }
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static <T> String joinApiValues(List<T> values, Function<T, String> apiMapper) {
        return values.stream()
            .map(apiMapper)
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
     * Builder for {@link DirectionsInput}.
     */
    public static final class Builder {
        private final DirectionsEndpoint origin;
        private final DirectionsEndpoint destination;
        private Optional<String> arrivalDate = Optional.empty();
        private List<DirectionsAvoid> avoid = List.of();
        private Optional<String> departureDate = Optional.empty();
        private Optional<String> language = Optional.empty();
        private Optional<Boolean> requestsAlternateRoutes = Optional.empty();
        private Optional<RouteLocation> searchLocation = Optional.empty();
        private Optional<SearchRegion> searchRegion = Optional.empty();
        private Optional<TransportType> transportType = Optional.empty();
        private Optional<RouteLocation> userLocation = Optional.empty();

        private Builder(DirectionsEndpoint origin, DirectionsEndpoint destination) {
            this.origin = Objects.requireNonNull(origin, "origin");
            this.destination = Objects.requireNonNull(destination, "destination");
        }

        /**
         * Sets the arrival date/time parameter (format as expected by the API).
         *
         * @param arrivalDate the arrival date/time, or {@code null} to clear
         * @return this builder
         */
        public Builder arrivalDate(String arrivalDate) {
            this.arrivalDate = Optional.ofNullable(arrivalDate);
            return this;
        }

        /**
         * Sets route features to avoid.
         *
         * @param avoid route features to avoid (empty means no avoid filters)
         * @return this builder
         */
        public Builder avoid(List<DirectionsAvoid> avoid) {
            this.avoid = normalizeList(avoid);
            return this;
        }

        /**
         * Sets the departure date/time parameter (format as expected by the API).
         *
         * @param departureDate the departure date/time, or {@code null} to clear
         * @return this builder
         */
        public Builder departureDate(String departureDate) {
            this.departureDate = Optional.ofNullable(departureDate);
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
         * Sets whether alternate routes should be requested.
         *
         * @param requestsAlternateRoutes {@code true} to request alternate routes, or {@code null} to clear
         * @return this builder
         */
        public Builder requestsAlternateRoutes(Boolean requestsAlternateRoutes) {
            this.requestsAlternateRoutes = Optional.ofNullable(requestsAlternateRoutes);
            return this;
        }

        /**
         * Sets the search location hint used by the API.
         *
         * @param searchLocation the search location, or {@code null} to clear
         * @return this builder
         */
        public Builder searchLocation(RouteLocation searchLocation) {
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
         * Sets the requested transport type.
         *
         * @param transportType the transport type, or {@code null} to clear
         * @return this builder
         */
        public Builder transportType(TransportType transportType) {
            this.transportType = Optional.ofNullable(transportType);
            return this;
        }

        /**
         * Sets the user location hint used by the API.
         *
         * @param userLocation the user location, or {@code null} to clear
         * @return this builder
         */
        public Builder userLocation(RouteLocation userLocation) {
            this.userLocation = Optional.ofNullable(userLocation);
            return this;
        }

        /**
         * Builds a validated {@link DirectionsInput}.
         *
         * @return an input instance
         */
        public DirectionsInput build() {
            return new DirectionsInput(
                origin,
                destination,
                arrivalDate,
                avoid,
                departureDate,
                language,
                requestsAlternateRoutes,
                searchLocation,
                searchRegion,
                transportType,
                userLocation
            );
        }
    }
}
