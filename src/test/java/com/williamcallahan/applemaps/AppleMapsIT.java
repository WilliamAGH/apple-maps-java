package com.williamcallahan.applemaps;

import com.williamcallahan.applemaps.domain.model.AutocompleteResult;
import com.williamcallahan.applemaps.domain.model.Place;
import com.williamcallahan.applemaps.domain.model.PlaceResults;
import com.williamcallahan.applemaps.domain.model.SearchAutocompleteResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponsePlace;
import com.williamcallahan.applemaps.domain.model.StructuredAddress;
import com.williamcallahan.applemaps.domain.request.GeocodeInput;
import com.williamcallahan.applemaps.domain.request.SearchAutocompleteInput;
import com.williamcallahan.applemaps.domain.request.SearchInput;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "APPLE_MAPS_TOKEN", matches = ".+")
class AppleMapsIT {
    private static final String TOKEN_ENVIRONMENT_VARIABLE = "APPLE_MAPS_TOKEN";
    private static final int REQUEST_TIMEOUT_SECONDS = 30;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS);
    private static final Locale TEXT_NORMALIZATION_LOCALE = Locale.ROOT;
    private static final String APPLE_PARK_NAME = "Apple Park";
    private static final String APPLE_PARK_PLACE_ID = "I7C250D2CDCB364A";
    private static final double APPLE_PARK_LATITUDE = 37.3346;
    private static final double APPLE_PARK_LONGITUDE = -122.0090;
    private static final String POINT_OF_INTEREST_GOLDEN_GATE_BRIDGE = "Golden Gate Bridge";
    private static final String POINT_OF_INTEREST_CENTRAL_PARK = "Central Park";
    private static final String TECH_STARTUP_STRIPE = "Stripe";
    private static final String TECH_STARTUP_AIRBNB = "Airbnb";
    private static final String PARTIAL_ADDRESS_QUERY = "Cupertino, CA";
    private static final String PARTIAL_ADDRESS_FRAGMENT = "Cupertino";
    private static final String FULL_ADDRESS_QUERY = "1 Apple Park Way, Cupertino, CA 95014";
    private static final String FULL_ADDRESS_FRAGMENT = "Apple Park";
    private static final List<String> POINT_OF_INTEREST_QUERIES = List.of(
        POINT_OF_INTEREST_GOLDEN_GATE_BRIDGE,
        POINT_OF_INTEREST_CENTRAL_PARK
    );
    private static final List<String> TECH_STARTUP_QUERIES = List.of(
        TECH_STARTUP_STRIPE,
        TECH_STARTUP_AIRBNB
    );

    private AppleMaps appleMapsClient;

    @BeforeEach
    void setUp() {
        String mapsToken = System.getenv(TOKEN_ENVIRONMENT_VARIABLE);
        appleMapsClient = new AppleMaps(mapsToken, REQUEST_TIMEOUT);
    }

    @AfterEach
    void tearDown() {
        if (appleMapsClient != null) {
            appleMapsClient.close();
        }
    }

    @Test
    void geocodeHandlesPartialAndFullAddresses() {
        GeocodeInput partialAddressInput = GeocodeInput.builder(PARTIAL_ADDRESS_QUERY).build();
        PlaceResults partialAddressResults = appleMapsClient.geocode(partialAddressInput);

        assertNotNull(partialAddressResults);
        assertFalse(partialAddressResults.results().isEmpty());
        assertPlaceResultsContainAddressFragment(partialAddressResults, PARTIAL_ADDRESS_FRAGMENT);

        GeocodeInput fullAddressInput = GeocodeInput.builder(FULL_ADDRESS_QUERY).build();
        PlaceResults fullAddressResults = appleMapsClient.geocode(fullAddressInput);

        assertNotNull(fullAddressResults);
        assertFalse(fullAddressResults.results().isEmpty());
        assertPlaceResultsContainAddressFragment(fullAddressResults, FULL_ADDRESS_FRAGMENT);
    }

    @Test
    void searchFindsPointsOfInterest() {
        assertSearchQueriesReturnMatches(POINT_OF_INTEREST_QUERIES);
    }

    @Test
    void searchFindsTechStartups() {
        assertSearchQueriesReturnMatches(TECH_STARTUP_QUERIES);
    }

    @Test
    void autocompleteAndResolve() {
        SearchAutocompleteInput autocompleteInput = SearchAutocompleteInput.builder(APPLE_PARK_NAME).build();
        SearchAutocompleteResponse autocompleteResponse = appleMapsClient.autocomplete(autocompleteInput);

        assertNotNull(autocompleteResponse);
        assertFalse(autocompleteResponse.results().isEmpty());

        List<AutocompleteResult> autocompleteResults = autocompleteResponse.results();
        boolean completionUrlsPresent = autocompleteResults.stream()
            .map(AutocompleteResult::completionUrl)
            .allMatch(url -> !url.isBlank());
        assertTrue(completionUrlsPresent);

        boolean displayLinesPresent = autocompleteResults.stream()
            .allMatch(autocompleteResult -> !autocompleteResult.displayLines().isEmpty());
        assertTrue(displayLinesPresent);

        List<SearchResponse> resolvedResponses = appleMapsClient.resolveCompletionUrls(autocompleteResponse);
        assertNotNull(resolvedResponses);
        assertEquals(autocompleteResults.size(), resolvedResponses.size());

        boolean resolvedHasPlaces = resolvedResponses.stream()
            .anyMatch(resolvedResponse -> !resolvedResponse.results().isEmpty());
        assertTrue(resolvedHasPlaces);
    }

    @Test
    void reverseGeocode() {
        PlaceResults reverseGeocodeResults = appleMapsClient.reverseGeocode(APPLE_PARK_LATITUDE, APPLE_PARK_LONGITUDE);

        assertNotNull(reverseGeocodeResults);
        assertFalse(reverseGeocodeResults.results().isEmpty());
    }

    @Test
    void lookupPlace() {
        Place place = appleMapsClient.lookupPlace(APPLE_PARK_PLACE_ID);

        assertNotNull(place);
        assertTrue(place.id().isPresent());
        assertEquals(APPLE_PARK_PLACE_ID, place.id().orElseThrow());
        assertEquals(APPLE_PARK_NAME, place.name());
    }

    private void assertSearchQueriesReturnMatches(List<String> searchQueries) {
        for (String searchQuery : searchQueries) {
            SearchInput searchInput = SearchInput.builder(searchQuery).build();
            SearchResponse searchResponse = appleMapsClient.search(searchInput);

            assertNotNull(searchResponse);
            assertFalse(searchResponse.results().isEmpty());
            assertSearchResponseContainsNameFragment(searchResponse, searchQuery);
        }
    }

    private static void assertSearchResponseContainsNameFragment(SearchResponse searchResponse, String expectedFragment) {
        String expectedFragmentLower = expectedFragment.toLowerCase(TEXT_NORMALIZATION_LOCALE);
        List<SearchResponsePlace> searchPlaces = searchResponse.results();
        boolean hasMatch = searchPlaces.stream()
            .anyMatch(searchPlace -> searchPlaceMatchesFragment(searchPlace, expectedFragmentLower));

        assertTrue(hasMatch);
    }

    private static boolean searchPlaceMatchesFragment(SearchResponsePlace searchPlace, String expectedFragmentLower) {
        if (textMatchesFragment(searchPlace.name(), expectedFragmentLower)) {
            return true;
        }

        boolean addressLineMatch = searchPlace.formattedAddressLines().stream()
            .anyMatch(addressLine -> textMatchesFragment(addressLine, expectedFragmentLower));
        if (addressLineMatch) {
            return true;
        }

        return searchPlace.structuredAddress()
            .map(structuredAddress -> structuredAddressMatchesFragment(structuredAddress, expectedFragmentLower))
            .orElse(false);
    }

    private static void assertPlaceResultsContainAddressFragment(PlaceResults placeResults, String expectedFragment) {
        String expectedFragmentLower = expectedFragment.toLowerCase(TEXT_NORMALIZATION_LOCALE);
        List<Place> places = placeResults.results();
        boolean hasMatch = places.stream()
            .anyMatch(place -> placeMatchesFragment(place, expectedFragmentLower));

        assertTrue(hasMatch);
    }

    private static boolean placeMatchesFragment(Place place, String expectedFragmentLower) {
        if (textMatchesFragment(place.name(), expectedFragmentLower)) {
            return true;
        }

        boolean addressLineMatch = place.formattedAddressLines().stream()
            .anyMatch(addressLine -> textMatchesFragment(addressLine, expectedFragmentLower));
        if (addressLineMatch) {
            return true;
        }

        return place.structuredAddress()
            .map(structuredAddress -> structuredAddressMatchesFragment(structuredAddress, expectedFragmentLower))
            .orElse(false);
    }

    private static boolean structuredAddressMatchesFragment(StructuredAddress structuredAddress, String expectedFragmentLower) {
        return optionalTextMatchesFragment(structuredAddress.administrativeArea(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.administrativeAreaCode(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.subAdministrativeArea(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.fullThoroughfare(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.locality(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.postCode(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.subLocality(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.subThoroughfare(), expectedFragmentLower)
            || optionalTextMatchesFragment(structuredAddress.thoroughfare(), expectedFragmentLower)
            || structuredAddress.areasOfInterest().stream()
                .anyMatch(areaOfInterest -> textMatchesFragment(areaOfInterest, expectedFragmentLower))
            || structuredAddress.dependentLocalities().stream()
                .anyMatch(dependentLocality -> textMatchesFragment(dependentLocality, expectedFragmentLower));
    }

    private static boolean optionalTextMatchesFragment(Optional<String> optionalText, String expectedFragmentLower) {
        return optionalText.map(text -> textMatchesFragment(text, expectedFragmentLower)).orElse(false);
    }

    private static boolean textMatchesFragment(String rawText, String expectedFragmentLower) {
        return rawText.toLowerCase(TEXT_NORMALIZATION_LOCALE).contains(expectedFragmentLower);
    }
}
