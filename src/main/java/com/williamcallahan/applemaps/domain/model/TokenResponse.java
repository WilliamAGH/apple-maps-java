package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * An object that contains an access token and its expiration time in seconds.
 */
public record TokenResponse(String accessToken, long expiresInSeconds) {
    public TokenResponse {
        Objects.requireNonNull(accessToken, "accessToken");
    }
}
