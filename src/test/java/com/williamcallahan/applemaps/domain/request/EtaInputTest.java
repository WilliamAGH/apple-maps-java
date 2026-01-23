package com.williamcallahan.applemaps.domain.request;

import com.williamcallahan.applemaps.domain.model.RouteLocation;
import com.williamcallahan.applemaps.domain.model.TransportType;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EtaInputTest {
    private static final RouteLocation ORIGIN = RouteLocation.fromLatitudeLongitude(37.331423, -122.030503);
    private static final List<RouteLocation> DESTINATIONS = List.of(
        RouteLocation.fromLatitudeLongitude(37.325565, -121.946352),
        RouteLocation.fromLatitudeLongitude(37.441765, -122.172593)
    );
    private static final String DEPARTURE_DATE = "2026-01-01T10:15:30Z";
    private static final String API_BASE_URI = "https://maps-api.apple.com/v1/etas";
    private static final String EXPECTED_QUERY =
        "?origin=37.331423%2C-122.030503&destinations=37.325565%2C-121.946352%7C37.441765%2C-122.172593" +
            "&transportType=Cycling&departureDate=2026-01-01T10%3A15%3A30Z";

    @Test
    void toQueryStringIncludesEtaParameters() {
        EtaInput etaInput = EtaInput.builder(ORIGIN, DESTINATIONS)
            .transportType(TransportType.CYCLING)
            .departureDate(DEPARTURE_DATE)
            .build();

        assertEquals(EXPECTED_QUERY, etaInput.toQueryString());
    }

    @Test
    void toQueryStringCreatesValidUri() {
        EtaInput etaInput = EtaInput.builder(ORIGIN, DESTINATIONS)
            .transportType(TransportType.CYCLING)
            .departureDate(DEPARTURE_DATE)
            .build();

        assertDoesNotThrow(() -> URI.create(API_BASE_URI + etaInput.toQueryString()));
    }

    @Test
    void buildRejectsEmptyDestinations() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> EtaInput.builder(ORIGIN, List.of()).build()
        );

        assertEquals("EtaInput destinations cannot be empty.", exception.getMessage());
    }
}
