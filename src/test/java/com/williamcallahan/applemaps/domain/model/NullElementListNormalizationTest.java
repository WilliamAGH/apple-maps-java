package com.williamcallahan.applemaps.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NullElementListNormalizationTest {
    private static final String PRIMARY_ID = "primary-id";
    private static final String ALTERNATE_ID = "alternate-id";
    private static final String COMPLETION_URL = "/search?q=test";
    private static final String ADDRESS_LINE = "Line 1";
    private static final String PLACE_NAME = "Test Place";
    private static final String COUNTRY_NAME = "United States";
    private static final String COUNTRY_CODE = "US";
    private static final String AREA_OF_INTEREST = "Area 1";
    private static final double LOCATION_LATITUDE = 37.7;
    private static final double LOCATION_LONGITUDE = -122.4;
    private static final double MAP_NORTH_LATITUDE = 38.0;
    private static final double MAP_EAST_LONGITUDE = -122.0;
    private static final double MAP_SOUTH_LATITUDE = 37.0;
    private static final double MAP_WEST_LONGITUDE = -123.0;
    private static final int STEP_INDEX = 1;

    @Test
    void alternateIdsEntryFiltersNullAlternateIds() {
        List<String> alternateIdsWithNull = new ArrayList<>(List.of(ALTERNATE_ID));
        alternateIdsWithNull.add(null);

        AlternateIdsEntry alternateIdsEntry =
            new AlternateIdsEntry(Optional.of(PRIMARY_ID), alternateIdsWithNull);

        assertEquals(List.of(ALTERNATE_ID), alternateIdsEntry.alternateIds());
    }

    @Test
    void alternateIdsResponseFiltersNullElements() {
        List<AlternateIdsEntry> alternateIdEntriesWithNull =
            new ArrayList<>(List.of(buildAlternateIdsEntry()));
        alternateIdEntriesWithNull.add(null);
        List<PlaceLookupError> placeLookupErrorsWithNull =
            new ArrayList<>(List.of(buildPlaceLookupError()));
        placeLookupErrorsWithNull.add(null);

        AlternateIdsResponse alternateIdsResponse =
            new AlternateIdsResponse(alternateIdEntriesWithNull, placeLookupErrorsWithNull);

        assertEquals(List.of(buildAlternateIdsEntry()), alternateIdsResponse.results());
        assertEquals(List.of(buildPlaceLookupError()), alternateIdsResponse.errors());
    }

    @Test
    void autocompleteResultFiltersNullDisplayLines() {
        List<String> displayLinesWithNull = new ArrayList<>(List.of(ADDRESS_LINE));
        displayLinesWithNull.add(null);

        AutocompleteResult autocompleteResult = new AutocompleteResult(
            COMPLETION_URL,
            displayLinesWithNull,
            Optional.empty(),
            Optional.empty()
        );

        assertEquals(List.of(ADDRESS_LINE), autocompleteResult.displayLines());
    }

    @Test
    void searchAutocompleteResponseFiltersNullResults() {
        List<AutocompleteResult> autocompleteResultsWithNull =
            new ArrayList<>(List.of(buildAutocompleteResult()));
        autocompleteResultsWithNull.add(null);

        SearchAutocompleteResponse searchAutocompleteResponse =
            new SearchAutocompleteResponse(autocompleteResultsWithNull);

        assertEquals(List.of(buildAutocompleteResult()), searchAutocompleteResponse.results());
    }

    @Test
    void searchResponseFiltersNullResults() {
        List<SearchResponsePlace> searchPlacesWithNull =
            new ArrayList<>(List.of(buildSearchResponsePlace()));
        searchPlacesWithNull.add(null);

        SearchResponse searchResponse =
            new SearchResponse(Optional.empty(), Optional.empty(), searchPlacesWithNull);

        assertEquals(List.of(buildSearchResponsePlace()), searchResponse.results());
    }

    @Test
    void searchResponsePlaceFiltersNullLists() {
        List<String> alternateIdsWithNull = new ArrayList<>(List.of(ALTERNATE_ID));
        alternateIdsWithNull.add(null);
        List<String> addressLinesWithNull = new ArrayList<>(List.of(ADDRESS_LINE));
        addressLinesWithNull.add(null);

        SearchResponsePlace searchResponsePlace = new SearchResponsePlace(
            Optional.of(PRIMARY_ID),
            alternateIdsWithNull,
            PLACE_NAME,
            buildLocation(),
            Optional.of(buildMapRegion()),
            addressLinesWithNull,
            Optional.empty(),
            COUNTRY_NAME,
            COUNTRY_CODE,
            Optional.empty()
        );

        assertEquals(List.of(ALTERNATE_ID), searchResponsePlace.alternateIds());
        assertEquals(List.of(ADDRESS_LINE), searchResponsePlace.formattedAddressLines());
    }

    @Test
    void structuredAddressFiltersNullLists() {
        List<String> areasWithNull = new ArrayList<>(List.of(AREA_OF_INTEREST));
        areasWithNull.add(null);
        List<String> dependentLocalitiesWithNull = new ArrayList<>(List.of(AREA_OF_INTEREST));
        dependentLocalitiesWithNull.add(null);

        StructuredAddress structuredAddress = new StructuredAddress(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            areasWithNull,
            dependentLocalitiesWithNull,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );

        assertEquals(List.of(AREA_OF_INTEREST), structuredAddress.areasOfInterest());
        assertEquals(List.of(AREA_OF_INTEREST), structuredAddress.dependentLocalities());
    }

    @Test
    void placeFiltersNullLists() {
        List<String> alternateIdsWithNull = new ArrayList<>(List.of(ALTERNATE_ID));
        alternateIdsWithNull.add(null);
        List<String> addressLinesWithNull = new ArrayList<>(List.of(ADDRESS_LINE));
        addressLinesWithNull.add(null);

        Place place = new Place(
            Optional.of(PRIMARY_ID),
            alternateIdsWithNull,
            PLACE_NAME,
            buildLocation(),
            Optional.of(buildMapRegion()),
            addressLinesWithNull,
            Optional.empty(),
            COUNTRY_NAME,
            COUNTRY_CODE
        );

        assertEquals(List.of(ALTERNATE_ID), place.alternateIds());
        assertEquals(List.of(ADDRESS_LINE), place.formattedAddressLines());
    }

    @Test
    void placeResultsFiltersNullResults() {
        List<Place> placesWithNull = new ArrayList<>(List.of(buildPlace()));
        placesWithNull.add(null);

        PlaceResults placeResults = new PlaceResults(placesWithNull);

        assertEquals(List.of(buildPlace()), placeResults.results());
    }

    @Test
    void placesResponseFiltersNullLists() {
        List<Place> placesWithNull = new ArrayList<>(List.of(buildPlace()));
        placesWithNull.add(null);
        List<PlaceLookupError> placeLookupErrorsWithNull =
            new ArrayList<>(List.of(buildPlaceLookupError()));
        placeLookupErrorsWithNull.add(null);

        PlacesResponse placesResponse = new PlacesResponse(placesWithNull, placeLookupErrorsWithNull);

        assertEquals(List.of(buildPlace()), placesResponse.results());
        assertEquals(List.of(buildPlaceLookupError()), placesResponse.errors());
    }

    @Test
    void directionsRouteFiltersNullStepIndexes() {
        List<Integer> stepIndexesWithNull = new ArrayList<>(List.of(STEP_INDEX));
        stepIndexesWithNull.add(null);

        DirectionsRoute directionsRoute = new DirectionsRoute(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            stepIndexesWithNull,
            Optional.empty()
        );

        assertEquals(List.of(STEP_INDEX), directionsRoute.stepIndexes());
    }

    @Test
    void directionsResponseFiltersNullRoutesAndSteps() {
        List<DirectionsRoute> routesWithNull = new ArrayList<>(List.of(buildDirectionsRoute()));
        routesWithNull.add(null);
        List<DirectionsStep> stepsWithNull = new ArrayList<>(List.of(buildDirectionsStep()));
        stepsWithNull.add(null);

        DirectionsResponse directionsResponse = new DirectionsResponse(
            Optional.empty(),
            Optional.empty(),
            routesWithNull,
            stepsWithNull,
            List.of()
        );

        assertEquals(List.of(buildDirectionsRoute()), directionsResponse.routes());
        assertEquals(List.of(buildDirectionsStep()), directionsResponse.steps());
    }

    @Test
    void directionsResponsePreservesStepPathsIndexAlignment() {
        // stepPaths preserves null entries as empty lists to maintain index alignment with steps
        List<Location> stepPath = new ArrayList<>(List.of(buildLocation()));
        stepPath.add(null); // null locations within a path ARE filtered
        List<List<Location>> stepPathsWithNull = new ArrayList<>(List.of(stepPath));
        stepPathsWithNull.add(null); // null paths are converted to empty lists (not filtered)

        DirectionsResponse directionsResponse = new DirectionsResponse(
            Optional.empty(),
            Optional.empty(),
            List.of(),
            List.of(),
            stepPathsWithNull
        );

        // Expect 2 entries: [validLocation] and [] (empty list for null path)
        assertEquals(2, directionsResponse.stepPaths().size());
        assertEquals(List.of(buildLocation()), directionsResponse.stepPaths().get(0));
        assertEquals(List.of(), directionsResponse.stepPaths().get(1));
    }

    @Test
    void errorResponseFiltersNullDetails() {
        List<String> detailsWithNull = new ArrayList<>(List.of(ADDRESS_LINE));
        detailsWithNull.add(null);

        ErrorResponse errorResponse = new ErrorResponse(PLACE_NAME, detailsWithNull);

        assertEquals(List.of(ADDRESS_LINE), errorResponse.details());
    }

    @Test
    void etaResponseFiltersNullEtas() {
        List<EtaEstimate> etaEstimatesWithNull = new ArrayList<>(List.of(buildEtaEstimate()));
        etaEstimatesWithNull.add(null);

        EtaResponse etaResponse = new EtaResponse(etaEstimatesWithNull);

        assertEquals(List.of(buildEtaEstimate()), etaResponse.etas());
    }

    private static AlternateIdsEntry buildAlternateIdsEntry() {
        return new AlternateIdsEntry(Optional.of(PRIMARY_ID), List.of(ALTERNATE_ID));
    }

    private static PlaceLookupError buildPlaceLookupError() {
        return new PlaceLookupError(PlaceLookupErrorCode.FAILED_INTERNAL_ERROR, Optional.empty());
    }

    private static AutocompleteResult buildAutocompleteResult() {
        return new AutocompleteResult(
            COMPLETION_URL,
            List.of(ADDRESS_LINE),
            Optional.empty(),
            Optional.empty()
        );
    }

    private static SearchResponsePlace buildSearchResponsePlace() {
        return new SearchResponsePlace(
            Optional.of(PRIMARY_ID),
            List.of(ALTERNATE_ID),
            PLACE_NAME,
            buildLocation(),
            Optional.of(buildMapRegion()),
            List.of(ADDRESS_LINE),
            Optional.empty(),
            COUNTRY_NAME,
            COUNTRY_CODE,
            Optional.empty()
        );
    }

    private static Place buildPlace() {
        return new Place(
            Optional.of(PRIMARY_ID),
            List.of(ALTERNATE_ID),
            PLACE_NAME,
            buildLocation(),
            Optional.of(buildMapRegion()),
            List.of(ADDRESS_LINE),
            Optional.empty(),
            COUNTRY_NAME,
            COUNTRY_CODE
        );
    }

    private static DirectionsRoute buildDirectionsRoute() {
        return new DirectionsRoute(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            List.of(STEP_INDEX),
            Optional.empty()
        );
    }

    private static DirectionsStep buildDirectionsStep() {
        return new DirectionsStep(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

    private static EtaEstimate buildEtaEstimate() {
        return new EtaEstimate(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

    private static Location buildLocation() {
        return new Location(LOCATION_LATITUDE, LOCATION_LONGITUDE);
    }

    private static MapRegion buildMapRegion() {
        return new MapRegion(
            MAP_NORTH_LATITUDE,
            MAP_EAST_LONGITUDE,
            MAP_SOUTH_LATITUDE,
            MAP_WEST_LONGITUDE
        );
    }
}
