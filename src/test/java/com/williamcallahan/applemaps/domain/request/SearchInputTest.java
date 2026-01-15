package com.williamcallahan.applemaps.domain.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.williamcallahan.applemaps.domain.model.AddressCategory;
import com.williamcallahan.applemaps.domain.model.PoiCategory;
import com.williamcallahan.applemaps.domain.model.SearchLocation;
import com.williamcallahan.applemaps.domain.model.SearchRegion;
import com.williamcallahan.applemaps.domain.model.SearchRegionPriority;
import com.williamcallahan.applemaps.domain.model.SearchResultType;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchInputTest {

    private static final String QUERY = "cafe";
    private static final List<PoiCategory> EXCLUDED_POI = List.of(
        PoiCategory.PARK
    );
    private static final List<PoiCategory> INCLUDED_POI = List.of(
        PoiCategory.CAFE
    );
    private static final List<String> LIMIT_TO_COUNTRIES = List.of("DE");
    private static final List<SearchResultType> RESULT_TYPES = List.of(
        SearchResultType.POI
    );
    private static final List<AddressCategory> INCLUDE_ADDRESS = List.of(
        AddressCategory.LOCALITY
    );
    private static final List<AddressCategory> EXCLUDE_ADDRESS = List.of(
        AddressCategory.POSTAL_CODE
    );
    private static final String LANGUAGE = "en-US";
    private static final double HAMBURG_LONGITUDE = 10.0;
    private static final double HAMBURG_LATITUDE = 53.57;
    private static final double REGION_NORTH = 54.0;
    private static final double REGION_EAST = 10.5;
    private static final double REGION_SOUTH = 53.0;
    private static final double REGION_WEST = 9.5;
    private static final double USER_LONGITUDE = 10.2;
    private static final double USER_LATITUDE = 53.6;
    private static final SearchRegionPriority REGION_PRIORITY =
        SearchRegionPriority.REQUIRED;
    private static final boolean PAGINATION_ENABLED = true;
    private static final String PAGE_TOKEN = "page-1";
    private static final String EXPECTED_QUERY =
        "?q=cafe&excludePoiCategories=Park&includePoiCategories=Cafe&limitToCountries=DE" +
        "&resultTypeFilter=poi&includeAddressCategories=Locality&excludeAddressCategories=PostalCode" +
        "&lang=en-US&searchLocation=53.57,10.0&searchRegion=54.0,10.5,53.0,9.5" +
        "&userLocation=53.6,10.2&searchRegionPriority=required&enablePagination=true&pageToken=page-1";

    @Test
    void toQueryStringIncludesAllFiltersInOrder() {
        SearchInput searchInput = SearchInput.builder(QUERY)
            .excludePoiCategories(EXCLUDED_POI)
            .includePoiCategories(INCLUDED_POI)
            .limitToCountries(LIMIT_TO_COUNTRIES)
            .resultTypeFilter(RESULT_TYPES)
            .includeAddressCategories(INCLUDE_ADDRESS)
            .excludeAddressCategories(EXCLUDE_ADDRESS)
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
            .searchRegionPriority(REGION_PRIORITY)
            .enablePagination(PAGINATION_ENABLED)
            .pageToken(PAGE_TOKEN)
            .build();

        assertEquals(EXPECTED_QUERY, searchInput.toQueryString());
    }
}
