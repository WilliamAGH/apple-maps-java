package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.RouteLocation;
import com.williamcallahan.applemaps.domain.model.TransportType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Input parameters for ETA requests.
 */
public record EtaInput(
    RouteLocation origin,
    List<RouteLocation> destinations,
    Optional<TransportType> transportType,
    Optional<String> departureDate,
    Optional<String> arrivalDate
) {
    private static final int MAX_DESTINATIONS = 10;
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String DESTINATION_SEPARATOR = "|";
    private static final String PARAMETER_ORIGIN = "origin";
    private static final String PARAMETER_DESTINATIONS = "destinations";
    private static final String PARAMETER_TRANSPORT_TYPE = "transportType";
    private static final String PARAMETER_DEPARTURE_DATE = "departureDate";
    private static final String PARAMETER_ARRIVAL_DATE = "arrivalDate";

    public EtaInput {
        origin = Objects.requireNonNull(origin, "origin");
        destinations = normalizeList(destinations);
        transportType = normalizeOptional(transportType);
        departureDate = normalizeOptional(departureDate);
        arrivalDate = normalizeOptional(arrivalDate);
        validateDestinations(destinations);
    }

    public String toQueryString() {
        List<String> parameters = new ArrayList<>();
        parameters.add(formatParameter(PARAMETER_ORIGIN, origin.toQueryString()));
        parameters.add(formatParameter(PARAMETER_DESTINATIONS, joinDestinations(destinations)));
        transportType.ifPresent(transportMode -> parameters.add(formatParameter(PARAMETER_TRANSPORT_TYPE, transportMode.apiValue())));
        departureDate.ifPresent(departureDateText -> parameters.add(formatParameter(PARAMETER_DEPARTURE_DATE, encode(departureDateText))));
        arrivalDate.ifPresent(arrivalDateText -> parameters.add(formatParameter(PARAMETER_ARRIVAL_DATE, encode(arrivalDateText))));
        return QUERY_PREFIX + String.join(PARAMETER_SEPARATOR, parameters);
    }

    public static Builder builder(RouteLocation origin, List<RouteLocation> destinations) {
        return new Builder(origin, destinations);
    }

    private static void validateDestinations(List<RouteLocation> destinations) {
        if (destinations.isEmpty()) {
            throw new IllegalArgumentException("EtaInput destinations cannot be empty.");
        }
        if (destinations.size() > MAX_DESTINATIONS) {
            throw new IllegalArgumentException("EtaInput destinations cannot exceed " + MAX_DESTINATIONS + ".");
        }
    }

    private static <T> List<T> normalizeList(List<T> rawList) {
        return List.copyOf(Objects.requireNonNullElse(rawList, List.of()));
    }

    private static <T> Optional<T> normalizeOptional(Optional<T> optionalInput) {
        return Objects.requireNonNullElse(optionalInput, Optional.empty());
    }

    private static String joinDestinations(List<RouteLocation> destinations) {
        return destinations.stream()
            .map(RouteLocation::toQueryString)
            .reduce((left, right) -> left + DESTINATION_SEPARATOR + right)
            .orElse("");
    }

    private static String formatParameter(String name, String parameterText) {
        return name + "=" + parameterText;
    }

    private static String encode(String rawText) {
        return URLEncoder.encode(rawText, StandardCharsets.UTF_8);
    }

    public static final class Builder {
        private final RouteLocation origin;
        private final List<RouteLocation> destinations;
        private Optional<TransportType> transportType = Optional.empty();
        private Optional<String> departureDate = Optional.empty();
        private Optional<String> arrivalDate = Optional.empty();

        private Builder(RouteLocation origin, List<RouteLocation> destinations) {
            this.origin = Objects.requireNonNull(origin, "origin");
            this.destinations = normalizeList(destinations);
        }

        public Builder transportType(TransportType transportType) {
            this.transportType = Optional.ofNullable(transportType);
            return this;
        }

        public Builder departureDate(String departureDate) {
            this.departureDate = Optional.ofNullable(departureDate);
            return this;
        }

        public Builder arrivalDate(String arrivalDate) {
            this.arrivalDate = Optional.ofNullable(arrivalDate);
            return this;
        }

        public EtaInput build() {
            return new EtaInput(origin, destinations, transportType, departureDate, arrivalDate);
        }
    }
}
