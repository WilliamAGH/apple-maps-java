# Authorization (Apple Maps Server API)

To call the Apple Maps Server API, you must provide an Apple-issued **authorization token** (a JWT).
This SDK exchanges that long-lived JWT (e.g., 1 year) for short-lived access tokens (e.g., 30 minutes) automatically and refreshes as needed.

## Create an authorization token

1. Create a Maps Identifier and private key in your Apple Developer account.
2. Generate a JWT using Apple’s token maker (or generate one programmatically).

Apple documentation:
- Creating a Maps Identifier and a Private Key: https://developer.apple.com/documentation/mapkitjs/creating_a_maps_identifier_and_a_private_key
- Token maker: https://maps.developer.apple.com/token-maker

## Local development with `.env`

Option A (recommended): environment variable

```bash
export APPLE_MAPS_TOKEN="your-token"
```

Option B (local dev for this repo): `.env` fallback

This repo includes `.env-example`. Copy it to `.env` and paste your token:

```bash
cp .env-example .env
```

`.env` is ignored by git (so you don’t accidentally commit secrets).

This project’s Gradle build loads `.env` into **system properties**, which is mainly convenient for running tests locally.
In CI, prefer setting `APPLE_MAPS_TOKEN` as an environment variable.

## Supplying the token to the SDK

Your application can source the token from wherever you store secrets (env vars, a secrets manager, etc).
For example:

```java
String token = System.getenv("APPLE_MAPS_TOKEN");
if (token == null || token.isBlank()) {
    token = System.getProperty("APPLE_MAPS_TOKEN");
}

AppleMaps api = new AppleMaps(token);
```
