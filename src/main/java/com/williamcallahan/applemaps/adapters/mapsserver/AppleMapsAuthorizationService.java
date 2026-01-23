package com.williamcallahan.applemaps.adapters.mapsserver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import com.williamcallahan.applemaps.adapters.jackson.AppleMapsObjectMapperFactory;
import com.williamcallahan.applemaps.domain.model.TokenResponse;

import tools.jackson.databind.ObjectMapper;

/**
 * Exchanges authorization tokens for access tokens and caches them.
 */
public final class AppleMapsAuthorizationService {
    private static final String TOKEN_PATH = "/v1/token";
    private static final Duration ACCESS_TOKEN_GRACE_PERIOD = Duration.ofSeconds(30);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final URI tokenUri;
    private final Duration timeout;
    private final String authToken;
    private final String origin;
    private final Clock clock;
    private final ReentrantLock refreshLock = new ReentrantLock();
    private final AtomicReference<AccessToken> accessToken = new AtomicReference<>();

    /**
     * Creates a service that exchanges an authorization token for access tokens.
     *
     * @param authToken the Apple Maps Server API authorization token
     * @param timeout request timeout for token exchange
     * @param origin optional Origin header value for token requests
     */
    public AppleMapsAuthorizationService(String authToken, Duration timeout, String origin) {
        this(new Dependencies(authToken, timeout, origin));
    }

    AppleMapsAuthorizationService(Dependencies dependencies) {
        this.objectMapper = dependencies.objectMapper();
        this.httpClient = dependencies.httpClient();
        this.tokenUri = dependencies.tokenUri();
        this.timeout = dependencies.timeout();
        this.authToken = dependencies.authToken();
        this.origin = dependencies.origin();
        this.clock = dependencies.clock();
    }

    /**
     * Returns the configured Origin header value, if any.
     *
     * @return the Origin header value, or {@code null} when not set
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Returns a cached access token, refreshing it when needed.
     *
     * @return the access token string
     */
    public String getAccessToken() {
        AccessToken cachedToken = accessToken.get();
        if (cachedToken != null && !isExpiring(cachedToken)) {
            return cachedToken.tokenString();
        }
        refreshLock.lock();
        try {
            AccessToken refreshedToken = accessToken.get();
            if (refreshedToken == null || isExpiring(refreshedToken)) {
                refreshedToken = refreshAccessToken();
                accessToken.set(refreshedToken);
            }
            return refreshedToken.tokenString();
        } finally {
            refreshLock.unlock();
        }
    }

    private AccessToken refreshAccessToken() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .GET()
            .timeout(timeout)
            .uri(tokenUri)
            .setHeader("Authorization", "Bearer " + authToken);

        if (origin != null) {
            builder.setHeader("Origin", origin);
        }

        HttpRequest httpRequest = builder.build();
        try {
            HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                String responseBody = new String(response.body());
                throw new AppleMapsApiException("token", response.statusCode(), responseBody);
            }
            TokenResponse tokenResponse = objectMapper.readValue(response.body(), TokenResponse.class);
            Instant expiresAt = extractExpiry(tokenResponse.accessToken());
            return new AccessToken(tokenResponse.accessToken(), expiresAt);
        } catch (AppleMapsApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AppleMapsClientException("token", exception);
        }
    }

    private boolean isExpiring(AccessToken token) {
        Instant now = clock.instant();
        Instant refreshAt = token.expiresAt().minus(ACCESS_TOKEN_GRACE_PERIOD);
        return refreshAt.isBefore(now);
    }

    private Instant extractExpiry(String tokenString) {
        int firstDelimiter = tokenString.indexOf('.');
        int secondDelimiter = tokenString.indexOf('.', firstDelimiter + 1);
        if (firstDelimiter < 0 || secondDelimiter < 0) {
            throw new IllegalArgumentException("Access token is not a valid JWT");
        }
        String tokenBodyEncoded = tokenString.substring(firstDelimiter + 1, secondDelimiter);
        byte[] decodedTokenBody = Base64.getUrlDecoder().decode(tokenBodyEncoded);
        try {
            TokenClaims tokenClaims = objectMapper.readValue(decodedTokenBody, TokenClaims.class);
            return Instant.ofEpochSecond(tokenClaims.exp());
        } catch (Exception exception) {
            throw new AppleMapsClientException("token", exception);
        }
    }

    private record AccessToken(String tokenString, Instant expiresAt) {
    }

    private record TokenClaims(long exp) {
    }

    static final class Dependencies {
        private final ObjectMapper objectMapper;
        private final HttpClient httpClient;
        private final URI tokenUri;
        private final Duration timeout;
        private final String authToken;
        private final String origin;
        private final Clock clock;

        Dependencies(String authToken, Duration timeout, String origin) {
            this(new DependenciesConfig(
                AppleMapsObjectMapperFactory.create(),
                HttpClient.newHttpClient(),
                URI.create("https://maps-api.apple.com" + TOKEN_PATH),
                timeout,
                authToken,
                origin,
                Clock.systemUTC()
            ));
        }

        Dependencies(DependenciesConfig config) {
            this.objectMapper = Objects.requireNonNull(config.objectMapper(), "objectMapper");
            this.httpClient = Objects.requireNonNull(config.httpClient(), "httpClient");
            this.tokenUri = Objects.requireNonNull(config.tokenUri(), "tokenUri");
            this.timeout = Objects.requireNonNull(config.timeout(), "timeout");
            this.authToken = Objects.requireNonNull(config.authToken(), "authToken");
            this.origin = config.origin();
            this.clock = Objects.requireNonNull(config.clock(), "clock");
        }

        record DependenciesConfig(
            ObjectMapper objectMapper,
            HttpClient httpClient,
            URI tokenUri,
            Duration timeout,
            String authToken,
            String origin,
            Clock clock
        ) {
        }

        ObjectMapper objectMapper() {
            return objectMapper;
        }

        HttpClient httpClient() {
            return httpClient;
        }

        URI tokenUri() {
            return tokenUri;
        }

        Duration timeout() {
            return timeout;
        }

        String authToken() {
            return authToken;
        }

        String origin() {
            return origin;
        }

        Clock clock() {
            return clock;
        }
    }
}
