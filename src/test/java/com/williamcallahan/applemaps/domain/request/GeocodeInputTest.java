package com.williamcallahan.applemaps.domain.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.williamcallahan.applemaps.domain.model.SearchLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import java.util.List;
import org.junit.jupiter.api.Test;

class GeocodeInputTest {

    private static final String ADDRESS = "Jungfernstieg 1";
    private static final List<String> LIMIT_TO_COUNTRIES = List.of("DE", "US");
    private static final String LANGUAGE = "en-US";
    private static final double HAMBURG_LONGITUDE = 10.0;
    private static final double HAMBURG_LATITUDE = 53.57;
    private static final double REGION_NORTH = 54.0;
    private static final double REGION_EAST = 10.5;
    private static final double REGION_SOUTH = 53.0;
    private static final double REGION_WEST = 9.5;
    private static final double USER_LONGITUDE = 10.2;
    private static final double USER_LATITUDE = 53.6;
    private static final String EXPECTED_QUERY =
        "?q=Jungfernstieg+1&limitToCountries=DE,US&lang=en-US&searchLocation=53.57,10.0" +
        "&searchRegion=54.0,10.5,53.0,9.5&userLocation=53.6,10.2";

    @Test
    void toQueryStringIncludesOptionalParameters() {
        GeocodeInput geocodeInput = GeocodeInput.builder(ADDRESS)
            .limitToCountries(LIMIT_TO_COUNTRIES)
            .language(LANGUAGE)
            .searchLocation(
                SearchLocation.fromLatitudeLongitude(
                    HAMBURG_LATITUDE,
                    HAMBURG_LONGITUDE
                )
            )
            .searchRegion(
                SearchRegion.fromBounds(
                    REGION_NORTH,
                    REGION_EAST,
                    REGION_SOUTH,
                    REGION_WEST
                )
            )
            .userLocation(
                UserLocation.fromLatitudeLongitude(
                    USER_LATITUDE,
                    USER_LONGITUDE
                )
            )
            .build();

        assertEquals(EXPECTED_QUERY, geocodeInput.toQueryString());
    }
}
