# Apple Maps SDK for Java (Server)

A lightweight Java SDK for the Apple Maps Server API, with automatic access-token exchange/refresh.

## Requirements

- Java 17+
- Apple Developer Program membership (to create a Maps Identifier + private key).
- A long-lived Apple Maps authorization token (JWT). More: `docs/authorization.md`.

## Installation

Replace `0.1.3` with the latest release.

Note: this repo’s build uses a Gradle Java toolchain (Java 17). If you don’t have JDK 17 installed locally, Gradle will download it automatically.

### Gradle

```groovy
dependencies {
    implementation("com.williamcallahan:apple-maps-java:0.1.3")
}
```

### Maven

```xml
<dependency>
  <groupId>com.williamcallahan</groupId>
  <artifactId>apple-maps-java</artifactId>
  <version>0.1.3</version>
</dependency>
```

## Configure `APPLE_MAPS_TOKEN`

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
- Integration tests: `docs/tests.md`
- CLI usage: `docs/cli.md`

## Example: geocode Startup HQ (San Francisco)

```java
String token = System.getenv("APPLE_MAPS_TOKEN");
if (token == null || token.isBlank()) {
    token = System.getProperty("APPLE_MAPS_TOKEN");
}
if (token == null || token.isBlank()) {
    throw new IllegalStateException("Set APPLE_MAPS_TOKEN (env var) or APPLE_MAPS_TOKEN (system property).");
}

AppleMaps api = new AppleMaps(token);
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

## What’s included

- Geocode + reverse geocode
- Search + autocomplete (and `resolveCompletionUrls`)
- Directions + ETA
- Place lookup + alternate IDs
More examples: `docs/usage.md`

Common use case: business / startup search (name-only or name + address) via Search + Autocomplete.

## Compatibility

- Java (server/JVM): usable from any modern JVM app (for example Spring Boot, Spring AI, Quarkus, or plain Java) as long as you run on a compatible JDK.
- Target bytecode: built for Java 17 classfiles, so consuming projects must run on JDK 17+ (including JDK 25).
- Kotlin/JVM: works like any other Maven dependency (same JVM/JDK requirement as above).
- Android: not supported as-is because this library uses `java.net.http.HttpClient` (not part of the standard Android runtime).
- Kotlin Multiplatform (KMP) / iOS: KMP is a popular way to write Kotlin that runs on iOS, but iOS uses Kotlin/Native (not a JVM), so it cannot consume JVM bytecode artifacts like this. To support iOS, this would need a KMP-native implementation (for example using Ktor client) and publishing a multiplatform artifact.

## Quota (included limits)

Apple provides daily quotas per Apple Developer Program membership (for example, a service-call limit shared between MapKit JS service requests and Apple Maps Server API calls). When you exceed the daily service-call quota, Apple responds with HTTP `429 Too Many Requests`. Apple does not provide a self-serve way to purchase additional quota; for extra capacity, contact Apple via the Maps developer dashboard. https://maps.developer.apple.com/

## More from the author

[William Callahan](https://williamcallahan.com)

- [TUI4J](https://github.com/WilliamAGH/tui4j) (a modern TUI library for Java)
- [Brief](https://github.com/WilliamAGH/brief) (an open-source terminal AI chat app in Java)
