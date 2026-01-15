package com.williamcallahan.applemaps.domain.port;

import com.williamcallahan.applemaps.domain.model.AlternateIdsResponse;
import com.williamcallahan.applemaps.domain.model.DirectionsResponse;
import com.williamcallahan.applemaps.domain.model.EtaResponse;
import com.williamcallahan.applemaps.domain.model.Place;
import com.williamcallahan.applemaps.domain.model.PlaceResults;
import com.williamcallahan.applemaps.domain.model.PlacesResponse;
import com.williamcallahan.applemaps.domain.model.SearchAutocompleteResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponse;
import com.williamcallahan.applemaps.domain.request.AlternateIdsInput;
import com.williamcallahan.applemaps.domain.request.DirectionsInput;
import com.williamcallahan.applemaps.domain.request.EtaInput;
import com.williamcallahan.applemaps.domain.request.GeocodeInput;
import com.williamcallahan.applemaps.domain.request.PlaceLookupInput;
import com.williamcallahan.applemaps.domain.request.SearchAutocompleteInput;
import com.williamcallahan.applemaps.domain.request.SearchInput;

/**
 * Port for invoking Apple Maps Server API operations.
 */
public interface AppleMapsGateway {
    PlaceResults geocode(GeocodeInput input);

    SearchResponse search(SearchInput input);

    SearchAutocompleteResponse autocomplete(SearchAutocompleteInput input);

    SearchResponse resolveCompletionUrl(String completionUrl);

    PlaceResults reverseGeocode(double latitude, double longitude, String language);

    DirectionsResponse directions(DirectionsInput input);

    EtaResponse etas(EtaInput input);

    Place lookupPlace(String placeId, String language);

    PlacesResponse lookupPlaces(PlaceLookupInput input);

    AlternateIdsResponse lookupAlternateIds(AlternateIdsInput input);

    default void close() {
    }
}
