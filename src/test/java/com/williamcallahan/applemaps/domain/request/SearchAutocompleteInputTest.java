package com.williamcallahan.applemaps.domain.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.williamcallahan.applemaps.domain.model.AddressCategory;
import com.williamcallahan.applemaps.domain.model.PoiCategory;
import com.williamcallahan.applemaps.domain.model.SearchACResultType;
import com.williamcallahan.applemaps.domain.model.SearchLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.SearchRegionPriority;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchAutocompleteInputTest {

    private static final String QUERY = "eiffel";
    private static final List<PoiCategory> EXCLUDED_POI = List.of(
        PoiCategory.PARK
    );
    private static final List<PoiCategory> INCLUDED_POI = List.of(
        PoiCategory.CAFE
    );
    private static final List<String> LIMIT_TO_COUNTRIES = List.of("FR");
    private static final List<SearchACResultType> RESULT_TYPES = List.of(
        SearchACResultType.QUERY
    );
    private static final List<AddressCategory> INCLUDE_ADDRESS = List.of(
        AddressCategory.LOCALITY
    );
    private static final List<AddressCategory> EXCLUDE_ADDRESS = List.of(
        AddressCategory.COUNTRY
    );
    private static final String LANGUAGE = "en-US";
    private static final double PARIS_LONGITUDE = 2.2945;
    private static final double PARIS_LATITUDE = 48.8584;
    private static final double REGION_NORTH = 48.87;
    private static final double REGION_EAST = 2.3;
    private static final double REGION_SOUTH = 48.85;
    private static final double REGION_WEST = 2.29;
    private static final double USER_LONGITUDE = 2.295;
    private static final double USER_LATITUDE = 48.858;
    private static final SearchRegionPriority REGION_PRIORITY =
        SearchRegionPriority.DEFAULT;
    private static final String EXPECTED_QUERY =
        "?q=eiffel&excludePoiCategories=Park&includePoiCategories=Cafe&limitToCountries=FR" +
        "&resultTypeFilter=query&includeAddressCategories=Locality&excludeAddressCategories=Country" +
        "&lang=en-US&searchLocation=48.8584,2.2945&searchRegion=48.87,2.3,48.85,2.29" +
        "&userLocation=48.858,2.295&searchRegionPriority=default";

    @Test
    void toQueryStringIncludesAutocompleteFilters() {
        SearchAutocompleteInput autocompleteInput =
            SearchAutocompleteInput.builder(QUERY)
                .excludePoiCategories(EXCLUDED_POI)
                .includePoiCategories(INCLUDED_POI)
                .limitToCountries(LIMIT_TO_COUNTRIES)
                .resultTypeFilter(RESULT_TYPES)
                .includeAddressCategories(INCLUDE_ADDRESS)
                .excludeAddressCategories(EXCLUDE_ADDRESS)
                .language(LANGUAGE)
                .searchLocation(
                    SearchLocation.fromLatitudeLongitude(
                        PARIS_LATITUDE,
                        PARIS_LONGITUDE
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
                .searchRegionPriority(REGION_PRIORITY)
                .build();

        assertEquals(EXPECTED_QUERY, autocompleteInput.toQueryString());
    }
}
