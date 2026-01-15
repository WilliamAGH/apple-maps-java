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

    public AppleMaps(String authToken) {
        this(authToken, DEFAULT_TIMEOUT);
    }

    public AppleMaps(String authToken, Duration timeout) {
        Objects.requireNonNull(authToken, "authToken");
        Objects.requireNonNull(timeout, "timeout");
        this.gateway = new HttpAppleMapsGateway(authToken, timeout);
    }

    public AppleMaps(AppleMapsGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    public PlaceResults geocode(GeocodeInput input) {
        return gateway.geocode(input);
    }

    public SearchResponse search(SearchInput input) {
        return gateway.search(input);
    }

    public SearchAutocompleteResponse autocomplete(SearchAutocompleteInput input) {
        return gateway.autocomplete(input);
    }

    public SearchResponse resolveCompletionUrl(String completionUrl) {
        return gateway.resolveCompletionUrl(completionUrl);
    }

    public List<SearchResponse> resolveCompletionUrls(SearchAutocompleteResponse response) {
        Objects.requireNonNull(response, "response");
        return resolveCompletionUrls(response.results());
    }

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

    public PlaceResults reverseGeocode(double latitude, double longitude) {
        return gateway.reverseGeocode(latitude, longitude, DEFAULT_LANGUAGE);
    }

    public PlaceResults reverseGeocode(double latitude, double longitude, String language) {
        String resolvedLanguage = language == null || language.isBlank() ? DEFAULT_LANGUAGE : language;
        return gateway.reverseGeocode(latitude, longitude, resolvedLanguage);
    }

    public DirectionsResponse directions(DirectionsInput input) {
        return gateway.directions(input);
    }

    public EtaResponse etas(EtaInput input) {
        return gateway.etas(input);
    }

    public Place lookupPlace(String placeId) {
        return lookupPlace(placeId, DEFAULT_LANGUAGE);
    }

    public Place lookupPlace(String placeId, String language) {
        String resolvedLanguage = language == null || language.isBlank() ? DEFAULT_LANGUAGE : language;
        return gateway.lookupPlace(placeId, resolvedLanguage);
    }

    public PlacesResponse lookupPlaces(PlaceLookupInput input) {
        return gateway.lookupPlaces(input);
    }

    public AlternateIdsResponse lookupAlternateIds(AlternateIdsInput input) {
        return gateway.lookupAlternateIds(input);
    }

    @Override
    public void close() {
        gateway.close();
    }
}
