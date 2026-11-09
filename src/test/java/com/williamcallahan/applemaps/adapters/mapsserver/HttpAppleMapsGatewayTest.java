package com.williamcallahan.applemaps.adapters.mapsserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class HttpAppleMapsGatewayTest {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);

    @Test
    void defaultClientFailsNewConnectionsAfterTheConnectTimeout() {
        HttpAppleMapsGateway.Dependencies dependencies =
            new HttpAppleMapsGateway.Dependencies("auth-token", REQUEST_TIMEOUT, null, CONNECT_TIMEOUT);
        try {
            assertEquals(Optional.of(CONNECT_TIMEOUT), dependencies.httpClient().connectTimeout());
        } finally {
            dependencies.executorService().shutdownNow();
        }
    }
}
