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
    /**
     * Performs a geocode request.
     *
     * @param input request parameters
     * @return geocode results
     */
    PlaceResults geocode(GeocodeInput input);

    /**
     * Performs a text search request.
     *
     * @param input request parameters
     * @return search results
     */
    SearchResponse search(SearchInput input);

    /**
     * Performs an autocomplete request.
     *
     * @param input request parameters
     * @return autocomplete results
     */
    SearchAutocompleteResponse autocomplete(SearchAutocompleteInput input);

    /**
     * Resolves a completion URL returned from an autocomplete response.
     *
     * @param completionUrl the URL to resolve
     * @return search results for the completion URL
     */
    SearchResponse resolveCompletionUrl(String completionUrl);

    /**
     * Performs a reverse geocode request.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     * @param language response language (BCP 47)
     * @return reverse geocode results
     */
    PlaceResults reverseGeocode(double latitude, double longitude, String language);

    /**
     * Performs a directions request.
     *
     * @param input request parameters
     * @return directions results
     */
    DirectionsResponse directions(DirectionsInput input);

    /**
     * Performs an ETA request.
     *
     * @param input request parameters
     * @return ETA results
     */
    EtaResponse etas(EtaInput input);

    /**
     * Looks up a place by ID.
     *
     * @param placeId place identifier
     * @param language response language (BCP 47)
     * @return a place
     */
    Place lookupPlace(String placeId, String language);

    /**
     * Performs a place lookup request.
     *
     * @param input request parameters
     * @return places results
     */
    PlacesResponse lookupPlaces(PlaceLookupInput input);

    /**
     * Performs an alternate IDs lookup request.
     *
     * @param input request parameters
     * @return alternate IDs results
     */
    AlternateIdsResponse lookupAlternateIds(AlternateIdsInput input);

    /**
     * Releases any resources owned by the gateway.
     */
    default void close() {
    }
}
