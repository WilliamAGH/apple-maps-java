package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.DirectionsAvoid;
import com.williamcallahan.applemaps.domain.model.DirectionsEndpoint;
import com.williamcallahan.applemaps.domain.model.RouteLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.TransportType;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DirectionsInputTest {
    private static final DirectionsEndpoint ORIGIN = DirectionsEndpoint.fromAddress("1 Infinite Loop, Cupertino, CA");
    private static final DirectionsEndpoint DESTINATION = DirectionsEndpoint.fromLatitudeLongitude(37.3349, -122.009);
    private static final String ARRIVAL_DATE = "2026-01-01T12:00:00Z";
    private static final String DEPARTURE_DATE = "2026-01-01T10:00:00Z";
    private static final List<DirectionsAvoid> AVOID = List.of(DirectionsAvoid.TOLLS);
    private static final String LANGUAGE = "en-US";
    private static final RouteLocation SEARCH_LOCATION = RouteLocation.fromLatitudeLongitude(37.78, -122.42);
    private static final SearchRegion SEARCH_REGION = SearchRegion.fromBounds(38.0, -122.1, 37.5, -122.5);
    private static final TransportType TRANSPORT_TYPE = TransportType.CYCLING;
    private static final RouteLocation USER_LOCATION = RouteLocation.fromLatitudeLongitude(37.79, -122.41);
    private static final String EXPECTED_QUERY =
        "?origin=1+Infinite+Loop%2C+Cupertino%2C+CA&destination=37.3349%2C-122.009" +
            "&arrivalDate=2026-01-01T12%3A00%3A00Z&avoid=Tolls&lang=en-US" +
            "&requestsAlternateRoutes=true&searchLocation=37.78,-122.42" +
            "&searchRegion=38.0,-122.1,37.5,-122.5&transportType=Cycling" +
            "&userLocation=37.79,-122.41";

    @Test
    void toQueryStringIncludesDirectionsParameters() {
        DirectionsInput directionsInput = DirectionsInput.builder(ORIGIN, DESTINATION)
            .arrivalDate(ARRIVAL_DATE)
            .avoid(AVOID)
            .language(LANGUAGE)
            .requestsAlternateRoutes(true)
            .searchLocation(SEARCH_LOCATION)
            .searchRegion(SEARCH_REGION)
            .transportType(TRANSPORT_TYPE)
            .userLocation(USER_LOCATION)
            .build();

        assertEquals(EXPECTED_QUERY, directionsInput.toQueryString());
    }

    @Test
    void buildRejectsArrivalAndDepartureDate() {
        DirectionsInput.Builder builder = DirectionsInput.builder(ORIGIN, DESTINATION)
            .arrivalDate(ARRIVAL_DATE)
            .departureDate(DEPARTURE_DATE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }
}
