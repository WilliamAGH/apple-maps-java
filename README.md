[![Maven Central](https://img.shields.io/maven-central/v/com.williamcallahan/apple-maps-java)](https://central.sonatype.com/artifact/com.williamcallahan/apple-maps-java)
[![CI](https://github.com/WilliamAGH/apple-maps-java/actions/workflows/CI.yaml/badge.svg)](https://github.com/WilliamAGH/apple-maps-java/actions/workflows/CI.yaml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)
[![Context7](src/main/resources/static/img/context7-badge.svg)](https://context7.com/williamagh/apple-maps-java)
[![DeepWiki](src/main/resources/static/img/deepwiki-badge.svg)](https://deepwiki.com/WilliamAGH/apple-maps-java)

# Apple Maps Server SDK for Java

A lightweight, unofficial Java SDK for the [Apple Maps Server API](https://developer.apple.com/documentation/applemapsserverapi).
Designed for backend/JVM apps that need server-side geocoding, search, directions, or ETA via a REST API.

This SDK automatically exchanges your long-lived Apple-issued authorization token (JWT) for short-lived access tokens and refreshes as needed.

Note: this is **not** [MapKit](https://developer.apple.com/documentation/mapkit) (native UI SDK) or [MapKit JS](https://developer.apple.com/documentation/mapkitjs) (web UI SDK).
This project is not affiliated with Apple.

## Apple Maps ecosystem

Apple provides three primary ways to integrate Maps. This library supports #1 (Server API).

| Service | Purpose | Best for |
| :--- | :--- | :--- |
| [Apple Maps Server API](https://developer.apple.com/documentation/applemapsserverapi) | REST HTTP API | Backend services (Java/Kotlin/Python/etc.) that need geocoding, search, directions, or ETA data without a UI. |
| [MapKit](https://developer.apple.com/documentation/mapkit) | Native UI framework | iOS/macOS apps that need interactive maps. |
| [MapKit JS](https://developer.apple.com/documentation/mapkitjs) | JavaScript UI library | Web apps that need interactive maps in the browser. |

## Requirements

- Java 17+
- Apple Developer Program membership (to create a Maps Identifier + private key).
- A long-lived Apple Maps authorization token (JWT). More: `docs/authorization.md`.

## Installation

This repo’s build uses a Gradle Java toolchain (Java 17). If you don’t have JDK 17 installed locally, Gradle will download it automatically.

### Gradle

```groovy
dependencies {
    implementation("com.williamcallahan:apple-maps-java:0.1.4")
}
```

### Maven

```xml
<dependency>
  <groupId>com.williamcallahan</groupId>
  <artifactId>apple-maps-java</artifactId>
  <version>0.1.4</version>
</dependency>
```

## Configuration

### `APPLE_MAPS_TOKEN` (required)

Option A (recommended): environment variable

```bash
export APPLE_MAPS_TOKEN="your-token"
```

or via a JVM system property

```bash
java -DAPPLE_MAPS_TOKEN="your-token" -jar your-app.jar
```

Option B (local dev for this repo): `.env` fallback

```bash
cp .env-example .env
```

Then set `APPLE_MAPS_TOKEN=...` in `.env`. This repo’s Gradle build loads `.env` into system properties (useful for running integration tests locally). `.env` is ignored by git.

### `APPLE_MAPS_ORIGIN` (optional)

If your authorization token (JWT) was generated with a specific `origin` claim, set `APPLE_MAPS_ORIGIN` as well (this value is sent as the HTTP `Origin` header):

```bash
export APPLE_MAPS_ORIGIN="https://api.example.com"
```

## Verify your token (integration test)

Run the live integration test suite:

```bash
./gradlew testDetail --tests com.williamcallahan.applemaps.AppleMapsIT
```

Or run a single “smoke test”:

```bash
./gradlew testDetail --tests com.williamcallahan.applemaps.AppleMapsIT.geocodeHandlesPartialAndFullAddresses
```

Or run a one-off CLI query:

```bash
./gradlew cli --args='geocode "880 Harrison St, San Francisco, CA 94107"'
```

More:
- Authorization/token details: `docs/authorization.md`
- Integration tests: `docs/tests.md`
- CLI usage: `docs/cli.md`
- More examples: `docs/usage.md`

## Example: geocode Startup HQ (San Francisco)

```java
String token = System.getenv("APPLE_MAPS_TOKEN");
if (token == null || token.isBlank()) {
    token = System.getProperty("APPLE_MAPS_TOKEN");
}
if (token == null || token.isBlank()) {
    throw new IllegalStateException("Set APPLE_MAPS_TOKEN (env var) or APPLE_MAPS_TOKEN (system property).");
}

String origin = System.getenv("APPLE_MAPS_ORIGIN");
if (origin == null || origin.isBlank()) {
    origin = System.getProperty("APPLE_MAPS_ORIGIN");
}

AppleMaps api = (origin == null || origin.isBlank())
    ? new AppleMaps(token)
    : new AppleMaps(token, origin);
PlaceResults results = api.geocode(GeocodeInput.builder("880 Harrison St, San Francisco, CA 94107").build());
System.out.println(results);
```

Example response (trimmed):

```json
{
  "results": [
    {
      "name": "880 Harrison St",
      "coordinate": {
        "latitude": 37.7796095,
        "longitude": -122.4016725
      },
      "formattedAddressLines": [
        "880 Harrison St",
        "San Francisco, CA  94107",
        "United States"
      ],
      "structuredAddress": {
        "administrativeArea": "California",
        "administrativeAreaCode": "CA",
        "locality": "San Francisco",
        "postCode": "94107",
        "subLocality": "Yerba Buena",
        "thoroughfare": "Harrison St",
        "subThoroughfare": "880",
        "fullThoroughfare": "880 Harrison St",
        "dependentLocalities": [
          "Yerba Buena"
        ]
      },
      "countryCode": "US"
    }
  ]
}
```

## Supported Server API features

This SDK provides typed wrappers for common Apple Maps Server API operations:

- **Geocoding**: `geocode` and `reverseGeocode`
- **Search**: `search`
- **Autocomplete**: `autocomplete`, `resolveCompletionUrl`, and `resolveCompletionUrls`
- **Directions & ETA**: `directions` and `etas`
- **Places**: `lookupPlace`, `lookupPlaces`, and `lookupAlternateIds`

Common use case: business / startup search (name-only or name + address) via Search + Autocomplete.

## Compatibility

- Java (server/JVM): usable from any modern JVM app (for example Spring Boot, Spring AI, Quarkus, or plain Java) as long as you run on a compatible JDK.
- Target bytecode: built for Java 17 classfiles, so consuming projects must run on JDK 17+ (including JDK 25).
- Kotlin/JVM: works like any other Maven dependency (same JVM/JDK requirement as above).
- Android: not supported as-is because this library uses `java.net.http.HttpClient` (not part of the standard Android runtime).
- Kotlin Multiplatform (KMP) / iOS: KMP is a popular way to write Kotlin that runs on iOS, but iOS uses Kotlin/Native (not a JVM), so it cannot consume JVM bytecode artifacts like this. To support iOS, this would need a KMP-native implementation (for example using Ktor client) and publishing a multiplatform artifact.

## Quota (included limits)

Apple provides free daily quotas that are currently significantly more generous than Google Maps API. But you must have a paid Apple Developer Program membership, which costs $99/year for access to all its Apple ecosystem resources. This API is Apple hardware/software agnostic.

Apple applies a daily service-call limit per membership (for example, a quota shared between MapKit JS service requests and Apple Maps Server API calls). When you exceed the daily quota, Apple responds with HTTP `429 Too Many Requests`. Apple does not provide a self-serve way to purchase additional quota; for extra capacity, contact Apple via the [Maps developer dashboard](https://maps.developer.apple.com/).

## Built with Apple Maps Java

[![Brief with Apple Maps](src/main/resources/static/img/apple-maps-java-screenshot.png)](https://github.com/WilliamAGH/brief)

**[Brief](https://github.com/WilliamAGH/brief)** - Terminal AI chat client with `/locate` command powered by Apple Maps Java and rendered with [TUI4J](https://github.com/WilliamAGH/tui4j).

## More from the author

[William Callahan](https://williamcallahan.com)

- [TUI4J](https://github.com/WilliamAGH/tui4j) (a modern TUI library for Java)
- [Brief](https://github.com/WilliamAGH/brief) (an open-source terminal AI chat app in Java)
- [Other projects](https://williamcallahan.com/projects)
