package com.williamcallahan.applemaps.adapters.mapsserver;

import com.williamcallahan.applemaps.adapters.jackson.AppleMapsObjectMapperFactory;
import com.williamcallahan.applemaps.domain.model.AlternateIdsResponse;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import tools.jackson.databind.ObjectMapper;

/**
 * HTTP adapter for Apple Maps Server API operations.
 */
public final class HttpAppleMapsGateway implements AppleMapsGateway {
    private static final String HTTP_CLIENT_THREAD_NAME_PREFIX = "apple-maps-http-client-";
    private static final String API_SERVER = "https://maps-api.apple.com";
    private static final String GEOCODE_PATH = "/v1/geocode";
    private static final String SEARCH_PATH = "/v1/search";
    private static final String AUTOCOMPLETE_PATH = "/v1/searchAutocomplete";
    private static final String REVERSE_GEOCODE_PATH = "/v1/reverseGeocode";
    private static final String DIRECTIONS_PATH = "/v1/directions";
    private static final String ETAS_PATH = "/v1/etas";
    private static final String PLACE_PATH = "/v1/place";
    private static final String PLACE_ALTERNATE_IDS_PATH = "/v1/place/alternateIds";
    private static final String PARAMETER_LOCATION = "loc";
    private static final String PARAMETER_LANGUAGE = "lang";
    private static final String QUERY_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String LOCATION_SEPARATOR = ",";

    private final AppleMapsAuthorizationService authorizationService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Duration timeout;
    private final ExecutorService executorService;

    /**
     * Creates an HTTP gateway that calls the Apple Maps Server API.
     *
     * @param authToken the Apple Maps Server API authorization token
     * @param timeout request timeout
     */
    public HttpAppleMapsGateway(String authToken, Duration timeout) {
        this(new Dependencies(authToken, timeout));
    }

    HttpAppleMapsGateway(Dependencies dependencies) {
        this.authorizationService = dependencies.authorizationService();
        this.objectMapper = dependencies.objectMapper();
        this.httpClient = dependencies.httpClient();
        this.timeout = dependencies.timeout();
        this.executorService = dependencies.executorService();
    }

    @Override
    public PlaceResults geocode(GeocodeInput input) {
        return invokeApi("geocode", buildUri(GEOCODE_PATH, input.toQueryString()), PlaceResults.class);
    }

    @Override
    public SearchResponse search(SearchInput input) {
        return invokeApi("search", buildUri(SEARCH_PATH, input.toQueryString()), SearchResponse.class);
    }

    @Override
    public SearchAutocompleteResponse autocomplete(SearchAutocompleteInput input) {
        return invokeApi("searchAutocomplete", buildUri(AUTOCOMPLETE_PATH, input.toQueryString()), SearchAutocompleteResponse.class);
    }

    @Override
    public SearchResponse resolveCompletionUrl(String completionUrl) {
        Objects.requireNonNull(completionUrl, "completionUrl");
        return invokeApi("search", URI.create(API_SERVER + completionUrl), SearchResponse.class);
    }

    @Override
    public PlaceResults reverseGeocode(double latitude, double longitude, String language) {
        StringBuilder query = new StringBuilder();
        query.append(QUERY_PREFIX)
            .append(PARAMETER_LOCATION)
            .append("=")
            .append(latitude)
            .append(LOCATION_SEPARATOR)
            .append(longitude);
        String resolvedLanguage = Objects.requireNonNull(language, "language");
        if (!resolvedLanguage.isBlank()) {
            query.append(PARAMETER_SEPARATOR)
                .append(PARAMETER_LANGUAGE)
                .append("=")
                .append(resolvedLanguage);
        }
        return invokeApi("reverseGeocode", buildUri(REVERSE_GEOCODE_PATH, query.toString()), PlaceResults.class);
    }

    @Override
    public DirectionsResponse directions(DirectionsInput input) {
        return invokeApi("directions", buildUri(DIRECTIONS_PATH, input.toQueryString()), DirectionsResponse.class);
    }

    @Override
    public EtaResponse etas(EtaInput input) {
        return invokeApi("etas", buildUri(ETAS_PATH, input.toQueryString()), EtaResponse.class);
    }

    @Override
    public Place lookupPlace(String placeId, String language) {
        Objects.requireNonNull(placeId, "placeId");
        String resolvedLanguage = Objects.requireNonNull(language, "language");
        String queryString = "";
        if (!resolvedLanguage.isBlank()) {
            queryString = QUERY_PREFIX + PARAMETER_LANGUAGE + "=" + resolvedLanguage;
        }
        return invokeApi("place", URI.create(API_SERVER + PLACE_PATH + "/" + placeId + queryString), Place.class);
    }

    @Override
    public PlacesResponse lookupPlaces(PlaceLookupInput input) {
        return invokeApi("place", buildUri(PLACE_PATH, input.toQueryString()), PlacesResponse.class);
    }

    @Override
    public AlternateIdsResponse lookupAlternateIds(AlternateIdsInput input) {
        return invokeApi("placeAlternateIds", buildUri(PLACE_ALTERNATE_IDS_PATH, input.toQueryString()),
            AlternateIdsResponse.class);
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private URI buildUri(String path, String queryString) {
        return URI.create(API_SERVER + path + queryString);
    }

    private <T> T invokeApi(String operation, URI uri, Class<T> responseType) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .timeout(timeout)
            .setHeader("Authorization", "Bearer " + authorizationService.getAccessToken())
            .build();
        try {
            HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new AppleMapsApiException(operation, response.statusCode(), new String(response.body()));
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (AppleMapsApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AppleMapsClientException(operation, exception);
        }
    }

    static final class Dependencies {
        private final AppleMapsAuthorizationService authorizationService;
        private final ObjectMapper objectMapper;
        private final HttpClient httpClient;
        private final Duration timeout;
        private final ExecutorService executorService;

        Dependencies(String authToken, Duration timeout) {
            this(createDefaultDependenciesConfig(authToken, timeout));
        }

        Dependencies(DependenciesConfig config) {
            this.authorizationService = Objects.requireNonNull(config.authorizationService(), "authorizationService");
            this.objectMapper = Objects.requireNonNull(config.objectMapper(), "objectMapper");
            this.httpClient = Objects.requireNonNull(config.httpClient(), "httpClient");
            this.timeout = Objects.requireNonNull(config.timeout(), "timeout");
            this.executorService = Objects.requireNonNull(config.executorService(), "executorService");
        }

        record DependenciesConfig(
            AppleMapsAuthorizationService authorizationService,
            ObjectMapper objectMapper,
            HttpClient httpClient,
            Duration timeout,
            ExecutorService executorService
        ) {
        }

        private static DependenciesConfig createDefaultDependenciesConfig(String authToken, Duration timeout) {
            ThreadFactory httpClientThreadFactory = new ThreadFactory() {
                private final AtomicInteger httpClientThreadSequence = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable runnable) {
                    Thread httpClientThread = new Thread(runnable);
                    httpClientThread.setName(HTTP_CLIENT_THREAD_NAME_PREFIX + httpClientThreadSequence.getAndIncrement());
                    httpClientThread.setDaemon(true);
                    return httpClientThread;
                }
            };
            ExecutorService httpClientExecutorService = Executors.newCachedThreadPool(httpClientThreadFactory);
            HttpClient httpClient = HttpClient.newBuilder().executor(httpClientExecutorService).build();

            return new DependenciesConfig(
                new AppleMapsAuthorizationService(authToken, timeout),
                AppleMapsObjectMapperFactory.create(),
                httpClient,
                timeout,
                httpClientExecutorService
            );
        }

        AppleMapsAuthorizationService authorizationService() {
            return authorizationService;
        }

        ObjectMapper objectMapper() {
            return objectMapper;
        }

        HttpClient httpClient() {
            return httpClient;
        }

        Duration timeout() {
            return timeout;
        }

        ExecutorService executorService() {
            return executorService;
        }
    }
}
