package com.williamcallahan.applemaps;

import com.williamcallahan.applemaps.adapters.mapsserver.AppleMapsClientException;
import com.williamcallahan.applemaps.adapters.mapsserver.HttpAppleMapsGateway;
import com.williamcallahan.applemaps.domain.model.AlternateIdsResponse;
import com.williamcallahan.applemaps.domain.model.AutocompleteResult;
import com.williamcallahan.applemaps.domain.model.DirectionsResponse;
import com.williamcallahan.applemaps.domain.model.EtaResponse;
import com.williamcallahan.applemaps.domain.model.Place;
import com.williamcallahan.applemaps.domain.model.PlaceResults;
import com.williamcallahan.applemaps.domain.model.PlacesResponse;
import com.williamcallahan.applemaps.domain.model.SearchAutocompleteResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponse;
import com.williamcallahan.applemaps.domain.port.AppleMapsGateway;
import com.williamcallahan.applemaps.domain.request.AlternateIdsInput;
import com.williamcallahan.applemaps.domain.request.DirectionsInput;
import com.williamcallahan.applemaps.domain.request.EtaInput;
import com.williamcallahan.applemaps.domain.request.GeocodeInput;
import com.williamcallahan.applemaps.domain.request.PlaceLookupInput;
import com.williamcallahan.applemaps.domain.request.SearchAutocompleteInput;
import com.williamcallahan.applemaps.domain.request.SearchInput;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Entry point for Apple Maps Server API operations.
 */
public final class AppleMaps implements AutoCloseable {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final String DEFAULT_LANGUAGE = "en-US";

    private final AppleMapsGateway gateway;

    /**
     * Creates an {@link AppleMaps} client using the provided authorization token and a default timeout.
     *
     * @param authToken the Apple Maps Server API authorization token
     */
    public AppleMaps(String authToken) {
        this(authToken, DEFAULT_TIMEOUT);
    }

    /**
     * Creates an {@link AppleMaps} client using the provided authorization token and timeout.
     *
     * @param authToken the Apple Maps Server API authorization token
     * @param timeout request timeout
     */
    public AppleMaps(String authToken, Duration timeout) {
        Objects.requireNonNull(authToken, "authToken");
        Objects.requireNonNull(timeout, "timeout");
        this.gateway = new HttpAppleMapsGateway(authToken, timeout);
    }

    /**
     * Creates an {@link AppleMaps} client backed by a custom gateway implementation.
     *
     * @param gateway the gateway to use for API operations
     */
    public AppleMaps(AppleMapsGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    /**
     * Performs a geocode request.
     *
     * @param input geocode request parameters
     * @return geocode results
     */
    public PlaceResults geocode(GeocodeInput input) {
        return gateway.geocode(input);
    }

    /**
     * Performs a search request.
     *
     * @param input search request parameters
     * @return search results
     */
    public SearchResponse search(SearchInput input) {
        return gateway.search(input);
    }

    /**
     * Performs an autocomplete request.
     *
     * @param input autocomplete request parameters
     * @return autocomplete results
     */
    public SearchAutocompleteResponse autocomplete(SearchAutocompleteInput input) {
        return gateway.autocomplete(input);
    }

    /**
     * Resolves a completion URL returned from an autocomplete response.
     *
     * @param completionUrl completion URL to resolve
     * @return search results for the completion URL
     */
    public SearchResponse resolveCompletionUrl(String completionUrl) {
        return gateway.resolveCompletionUrl(completionUrl);
    }

    /**
     * Resolves all completion URLs from an autocomplete response.
     *
     * @param response an autocomplete response
     * @return resolved search responses in the same order as the results
     */
    public List<SearchResponse> resolveCompletionUrls(SearchAutocompleteResponse response) {
        Objects.requireNonNull(response, "response");
        return resolveCompletionUrls(response.results());
    }

    /**
     * Resolves all completion URLs from an autocomplete result list.
     *
     * @param results autocomplete results
     * @return resolved search responses in the same order as the results
     */
    public List<SearchResponse> resolveCompletionUrls(List<AutocompleteResult> results) {
        Objects.requireNonNull(results, "results");
        if (results.isEmpty()) {
            return List.of();
        }
        
        List<CompletableFuture<SearchResponse>> futures = results.stream()
            .map(result -> CompletableFuture.supplyAsync(() -> gateway.resolveCompletionUrl(result.completionUrl())))
            .toList();

        try {
            return futures.stream()
                .map(CompletableFuture::join)
                .toList();
        } catch (Exception exception) {
            Throwable cause = exception instanceof java.util.concurrent.CompletionException ? exception.getCause() : exception;

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new AppleMapsClientException("autocomplete", cause);
        }
    }

    /**
     * Performs a reverse geocode request using the default language ({@code en-US}).
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     * @return reverse geocode results
     */
    public PlaceResults reverseGeocode(double latitude, double longitude) {
        return gateway.reverseGeocode(latitude, longitude, DEFAULT_LANGUAGE);
    }

    /**
     * Performs a reverse geocode request.
     *
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     * @param language response language (BCP 47); if {@code null} or blank, defaults to {@code en-US}
     * @return reverse geocode results
     */
    public PlaceResults reverseGeocode(double latitude, double longitude, String language) {
        String resolvedLanguage = language == null || language.isBlank() ? DEFAULT_LANGUAGE : language;
        return gateway.reverseGeocode(latitude, longitude, resolvedLanguage);
    }

    /**
     * Performs a directions request.
     *
     * @param input directions request parameters
     * @return directions results
     */
    public DirectionsResponse directions(DirectionsInput input) {
        return gateway.directions(input);
    }

    /**
     * Performs an ETA request.
     *
     * @param input ETA request parameters
     * @return ETA results
     */
    public EtaResponse etas(EtaInput input) {
        return gateway.etas(input);
    }

    /**
     * Looks up a place by ID using the default language ({@code en-US}).
     *
     * @param placeId place identifier
     * @return a place
     */
    public Place lookupPlace(String placeId) {
        return lookupPlace(placeId, DEFAULT_LANGUAGE);
    }

    /**
     * Looks up a place by ID.
     *
     * @param placeId place identifier
     * @param language response language (BCP 47); if {@code null} or blank, defaults to {@code en-US}
     * @return a place
     */
    public Place lookupPlace(String placeId, String language) {
        String resolvedLanguage = language == null || language.isBlank() ? DEFAULT_LANGUAGE : language;
        return gateway.lookupPlace(placeId, resolvedLanguage);
    }

    /**
     * Looks up places using a place lookup input.
     *
     * @param input place lookup request parameters
     * @return places results
     */
    public PlacesResponse lookupPlaces(PlaceLookupInput input) {
        return gateway.lookupPlaces(input);
    }

    /**
     * Looks up alternate IDs for one or more places.
     *
     * @param input alternate IDs request parameters
     * @return alternate IDs results
     */
    public AlternateIdsResponse lookupAlternateIds(AlternateIdsInput input) {
        return gateway.lookupAlternateIds(input);
    }

    @Override
    public void close() {
        gateway.close();
    }
}
