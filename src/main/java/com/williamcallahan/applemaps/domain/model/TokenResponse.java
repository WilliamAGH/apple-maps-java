package com.williamcallahan.applemaps.domain.model;

import java.util.Objects;

/**
 * An object that contains an access token and its expiration time in seconds.
 */
public record TokenResponse(String accessToken, long expiresInSeconds) {
    /**
     * Canonical constructor that validates required fields.
     *
     * @param accessToken access token string
     * @param expiresInSeconds expiration time in seconds
     */
    public TokenResponse {
        Objects.requireNonNull(accessToken, "accessToken");
    }
}
