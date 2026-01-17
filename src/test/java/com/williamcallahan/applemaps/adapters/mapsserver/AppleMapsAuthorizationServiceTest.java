package com.williamcallahan.applemaps.adapters.mapsserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.PushPromiseHandler;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

import org.junit.jupiter.api.Test;

import com.williamcallahan.applemaps.adapters.jackson.AppleMapsObjectMapperFactory;

class AppleMapsAuthorizationServiceTest {
    private static final URI TOKEN_URI = URI.create("https://maps-api.apple.com/v1/token");
    private static final String AUTH_TOKEN = "auth-token";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final Instant START_TIME = Instant.parse("2026-01-01T00:00:00Z");
    private static final Duration SHORT_LIVED_TOKEN = Duration.ofSeconds(40);
    private static final Duration LONG_LIVED_TOKEN = Duration.ofSeconds(120);
    private static final Duration CLOCK_ADVANCE = Duration.ofSeconds(60);
    private static final int TOKEN_RESPONSE_CODE = 200;
    private static final long EXPIRY_SECONDS = 1800L;
    private static final String JWT_HEADER_JSON = "{\"alg\":\"none\"}";

    @Test
    void getAccessTokenCachesUntilExpiring() {
        MutableClock tokenClock = new MutableClock(START_TIME);
        StubHttpClient stubHttpClient = new StubHttpClient();
        stubHttpClient.enqueue(TOKEN_RESPONSE_CODE, tokenResponse(jwtWithExp(tokenClock.instant().plus(LONG_LIVED_TOKEN))));
        AppleMapsAuthorizationService authorizationService = new AppleMapsAuthorizationService(
            new AppleMapsAuthorizationService.Dependencies(
                new AppleMapsAuthorizationService.Dependencies.DependenciesConfig(
                    AppleMapsObjectMapperFactory.create(),
                    stubHttpClient,
                    TOKEN_URI,
                    REQUEST_TIMEOUT,
                    AUTH_TOKEN,
                    "origin",
                    tokenClock
                )
            )
        );

        String firstAccessToken = authorizationService.getAccessToken();
        String cachedAccessToken = authorizationService.getAccessToken();

        assertEquals(firstAccessToken, cachedAccessToken);
        assertEquals(1, stubHttpClient.requestCount());
    }

    @Test
    void getAccessTokenRefreshesAfterExpiry() {
        MutableClock tokenClock = new MutableClock(START_TIME);
        StubHttpClient stubHttpClient = new StubHttpClient();
        stubHttpClient.enqueue(TOKEN_RESPONSE_CODE, tokenResponse(jwtWithExp(tokenClock.instant().plus(SHORT_LIVED_TOKEN))));
        stubHttpClient.enqueue(TOKEN_RESPONSE_CODE, tokenResponse(jwtWithExp(tokenClock.instant().plus(LONG_LIVED_TOKEN))));
        AppleMapsAuthorizationService authorizationService = new AppleMapsAuthorizationService(
            new AppleMapsAuthorizationService.Dependencies(
                new AppleMapsAuthorizationService.Dependencies.DependenciesConfig(
                    AppleMapsObjectMapperFactory.create(),
                    stubHttpClient,
                    TOKEN_URI,
                    REQUEST_TIMEOUT,
                    AUTH_TOKEN,
                    "origin",
                    tokenClock
                )
            )
        );

        authorizationService.getAccessToken();
        tokenClock.advance(CLOCK_ADVANCE);
        authorizationService.getAccessToken();

        assertEquals(2, stubHttpClient.requestCount());
    }

    private static String tokenResponse(String accessToken) {
        return "{\"accessToken\":\"" + accessToken + "\",\"expiresInSeconds\":" + EXPIRY_SECONDS + "}";
    }

    private static String jwtWithExp(Instant expiration) {
        String header = Base64Url.encode(JWT_HEADER_JSON);
        String payload = Base64Url.encode("{\"exp\":" + expiration.getEpochSecond() + "}");
        return header + "." + payload + ".";
    }

    private static final class Base64Url {
        private static String encode(String payload) {
            return java.util.Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static final class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zone;

        private MutableClock(Instant instant) {
            this(instant, ZoneOffset.UTC);
        }

        private MutableClock(Instant instant, ZoneId zone) {
            this.instant = instant;
            this.zone = zone;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    private static final class StubHttpClient extends HttpClient {
        private final Queue<StubTokenResponse> queuedResponses = new ArrayDeque<>();
        private final AtomicInteger requestCount = new AtomicInteger();

        void enqueue(int statusCode, String responseBody) {
            queuedResponses.add(new StubTokenResponse(statusCode, responseBody.getBytes(StandardCharsets.UTF_8)));
        }

        int requestCount() {
            return requestCount.get();
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> responseBodyHandler) {
            requestCount.incrementAndGet();
            StubTokenResponse tokenResponse = queuedResponses.remove();
            return buildResponse(request, responseBodyHandler, tokenResponse);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
            HttpRequest request,
            BodyHandler<T> responseBodyHandler
        ) {
            try {
                return CompletableFuture.completedFuture(send(request, responseBodyHandler));
            } catch (Exception exception) {
                CompletableFuture<HttpResponse<T>> future = new CompletableFuture<>();
                future.completeExceptionally(exception);
                return future;
            }
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
            HttpRequest request,
            BodyHandler<T> responseBodyHandler,
            PushPromiseHandler<T> pushPromiseHandler
        ) {
            return sendAsync(request, responseBodyHandler);
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            try {
                return SSLContext.getDefault();
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to create SSLContext", exception);
            }
        }

        @Override
        public SSLParameters sslParameters() {
            return new SSLParameters();
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }

        @Override
        public Optional<Executor> executor() {
            return Optional.empty();
        }

        private static <T> HttpResponse<T> buildResponse(
            HttpRequest request,
            BodyHandler<T> responseBodyHandler,
            StubTokenResponse tokenResponse
        ) {
            HttpHeaders headers = HttpHeaders.of(Map.of(), (ignoredName, ignoredHeader) -> true);
            ResponseInfo responseInfo = new StubResponseInfo(tokenResponse.statusCode(), headers);
            BodySubscriber<T> bodySubscriber = responseBodyHandler.apply(responseInfo);
            bodySubscriber.onSubscribe(new ImmediateSubscription());
            bodySubscriber.onNext(List.of(ByteBuffer.wrap(tokenResponse.responseBody())));
            bodySubscriber.onComplete();
            T responseBody = bodySubscriber.getBody().toCompletableFuture().join();
            return new StubHttpResponse<>(tokenResponse.statusCode(), responseBody, headers, request);
        }
    }

    private record StubTokenResponse(int statusCode, byte[] responseBody) {
    }

    private record StubResponseInfo(int statusCode, HttpHeaders headers) implements ResponseInfo {
        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }

    private static final class ImmediateSubscription implements Flow.Subscription {
        @Override
        public void request(long requested) {
        }

        @Override
        public void cancel() {
        }
    }

    private record StubHttpResponse<T>(
        int statusCode,
        T body,
        HttpHeaders headers,
        HttpRequest request
    ) implements HttpResponse<T> {
        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return request.uri();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
