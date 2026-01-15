package com.williamcallahan.applemaps.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.williamcallahan.applemaps.AppleMaps;
import com.williamcallahan.applemaps.domain.model.AlternateIdsResponse;
import com.williamcallahan.applemaps.domain.model.DirectionsResponse;
import com.williamcallahan.applemaps.domain.model.EtaResponse;
import com.williamcallahan.applemaps.domain.model.Location;
import com.williamcallahan.applemaps.domain.model.Place;
import com.williamcallahan.applemaps.domain.model.PlaceResults;
import com.williamcallahan.applemaps.domain.model.PlacesResponse;
import com.williamcallahan.applemaps.domain.model.SearchAutocompleteResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponse;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import com.williamcallahan.applemaps.domain.port.AppleMapsGateway;
import com.williamcallahan.applemaps.domain.request.AlternateIdsInput;
import com.williamcallahan.applemaps.domain.request.DirectionsInput;
import com.williamcallahan.applemaps.domain.request.EtaInput;
import com.williamcallahan.applemaps.domain.request.GeocodeInput;
import com.williamcallahan.applemaps.domain.request.PlaceLookupInput;
import com.williamcallahan.applemaps.domain.request.SearchAutocompleteInput;
import com.williamcallahan.applemaps.domain.request.SearchInput;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AppleMapsCliTest {

    @Test
    void toApiUrlPrefixesApiHost() {
        String apiUrl = AppleMapsCli.toApiUrl("/v1/search?q=Apple%20Park");
        assertEquals(
            "https://maps-api.apple.com/v1/search?q=Apple%20Park",
            apiUrl
        );
    }

    @Test
    void toApiUrlAcceptsAbsoluteApiUrls() {
        String apiUrl = AppleMapsCli.toApiUrl(
            "https://maps-api.apple.com/v1/search?q=Apple%20Park"
        );
        assertEquals(
            "https://maps-api.apple.com/v1/search?q=Apple%20Park",
            apiUrl
        );
    }

    @Test
    void toAppleMapsWebUrlBuildsBrowserUrlFromCompletionUrl() {
        Optional<String> webUrl = AppleMapsCli.toAppleMapsWebUrl(
            "/v1/search?q=Apple%20Park"
        );
        assertEquals(
            Optional.of("https://maps.apple.com/?q=Apple%20Park"),
            webUrl
        );
    }

    @Test
    void parseUserLocationReadsLatitudeLongitudeCsv() {
        Optional<UserLocation> userLocation = AppleMapsCli.parseUserLocation(
            "37.7,-122.4"
        );
        assertTrue(userLocation.isPresent());
        assertEquals("37.7,-122.4", userLocation.orElseThrow().toQueryString());
    }

    @Test
    void parseUserLocationRejectsInvalidText() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            AppleMapsCli.parseUserLocation("not-a-coordinate")
        );
        assertTrue(exception.getMessage().contains("APPLE_MAPS_USER_LOCATION"));
    }

    @Test
    void geocodeFirstResultToUserLocationUsesFirstGeocodeCoordinate() {
        AppleMaps api = new AppleMaps(new SinglePlaceGateway());
        GeocodeInput geocodeInput = GeocodeInput.builder(
            "San Francisco, CA"
        ).build();

        UserLocation userLocation =
            AppleMapsCli.geocodeFirstResultToUserLocation(api, geocodeInput);
        assertEquals("37.7,-122.4", userLocation.toQueryString());
    }

    @Test
    void geocodeFirstResultToUserLocationRejectsEmptyGeocodeResults() {
        AppleMaps api = new AppleMaps(new EmptyGeocodeGateway());
        GeocodeInput geocodeInput = GeocodeInput.builder("Nowhere").build();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            AppleMapsCli.geocodeFirstResultToUserLocation(api, geocodeInput)
        );
        assertTrue(
            exception.getMessage().contains("APPLE_MAPS_USER_LOCATION_QUERY")
        );
    }

    private static final class SinglePlaceGateway implements AppleMapsGateway {

        @Override
        public PlaceResults geocode(GeocodeInput geocodeInput) {
            Place place = new Place(
                Optional.empty(),
                List.of(),
                "San Francisco",
                new Location(37.7, -122.4),
                Optional.empty(),
                List.of("San Francisco, CA", "United States"),
                Optional.empty(),
                "United States",
                "US"
            );
            return new PlaceResults(List.of(place));
        }

        @Override
        public SearchResponse search(SearchInput searchInput) {
            throw new UnsupportedOperationException("search");
        }

        @Override
        public SearchAutocompleteResponse autocomplete(
            SearchAutocompleteInput autocompleteInput
        ) {
            throw new UnsupportedOperationException("autocomplete");
        }

        @Override
        public SearchResponse resolveCompletionUrl(String completionUrl) {
            throw new UnsupportedOperationException("resolveCompletionUrl");
        }

        @Override
        public PlaceResults reverseGeocode(
            double latitude,
            double longitude,
            String language
        ) {
            throw new UnsupportedOperationException("reverseGeocode");
        }

        @Override
        public DirectionsResponse directions(DirectionsInput directionsInput) {
            throw new UnsupportedOperationException("directions");
        }

        @Override
        public EtaResponse etas(EtaInput etaInput) {
            throw new UnsupportedOperationException("etas");
        }

        @Override
        public Place lookupPlace(String placeId, String language) {
            throw new UnsupportedOperationException("lookupPlace");
        }

        @Override
        public PlacesResponse lookupPlaces(PlaceLookupInput placeLookupInput) {
            throw new UnsupportedOperationException("lookupPlaces");
        }

        @Override
        public AlternateIdsResponse lookupAlternateIds(
            AlternateIdsInput alternateIdsInput
        ) {
            throw new UnsupportedOperationException("lookupAlternateIds");
        }
    }

    private static final class EmptyGeocodeGateway implements AppleMapsGateway {

        @Override
        public PlaceResults geocode(GeocodeInput geocodeInput) {
            return new PlaceResults(List.of());
        }

        @Override
        public SearchResponse search(SearchInput searchInput) {
            throw new UnsupportedOperationException("search");
        }

        @Override
        public SearchAutocompleteResponse autocomplete(
            SearchAutocompleteInput autocompleteInput
        ) {
            throw new UnsupportedOperationException("autocomplete");
        }

        @Override
        public SearchResponse resolveCompletionUrl(String completionUrl) {
            throw new UnsupportedOperationException("resolveCompletionUrl");
        }

        @Override
        public PlaceResults reverseGeocode(
            double latitude,
            double longitude,
            String language
        ) {
            throw new UnsupportedOperationException("reverseGeocode");
        }

        @Override
        public DirectionsResponse directions(DirectionsInput directionsInput) {
            throw new UnsupportedOperationException("directions");
        }

        @Override
        public EtaResponse etas(EtaInput etaInput) {
            throw new UnsupportedOperationException("etas");
        }

        @Override
        public Place lookupPlace(String placeId, String language) {
            throw new UnsupportedOperationException("lookupPlace");
        }

        @Override
        public PlacesResponse lookupPlaces(PlaceLookupInput placeLookupInput) {
            throw new UnsupportedOperationException("lookupPlaces");
        }

        @Override
        public AlternateIdsResponse lookupAlternateIds(
            AlternateIdsInput alternateIdsInput
        ) {
            throw new UnsupportedOperationException("lookupAlternateIds");
        }
    }
}
