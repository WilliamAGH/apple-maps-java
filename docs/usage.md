# Usage

## Quick start

```java
String token = System.getenv("APPLE_MAPS_TOKEN");
if (token == null || token.isBlank()) {
    token = System.getProperty("APPLE_MAPS_TOKEN");
}

AppleMaps api = new AppleMaps(token);

GeocodeInput input = GeocodeInput.builder("Jungfernstieg 1").build();
PlaceResults results = api.geocode(input);
System.out.println(results);
```

## APIs

### Geocode

Transform a street address into coordinates and a structured address.

```java
PlaceResults results = api.geocode(
    GeocodeInput.builder("Jungfernstieg 1").build()
);
```

### Reverse geocode

Transform coordinates into a structured address.

```java
PlaceResults results = api.reverseGeocode(53.551666, 9.9942163);
```

### Autocomplete

Get a list of address / POI completions for a fuzzy prompt.

```java
SearchAutocompleteResponse response = api.autocomplete(
    SearchAutocompleteInput.builder("Dorfstr").build()
);
```

### Search

Search for places (addresses or POIs).

```java
SearchResponse response = api.search(
    SearchInput.builder("coffee").build()
);
```

## Use case: find a business / startup

This SDK is a good fit for “find a company” UX (name-only queries, partial addresses, office locations).

### Name only

Use Autocomplete for typeahead, then resolve to get place details:

```java
SearchAutocompleteResponse autocomplete = api.autocomplete(
    SearchAutocompleteInput.builder("Stripe").build()
);

List<SearchResponse> resolved = api.resolveCompletionUrls(autocomplete);
```

### Name + city / region

Use Search when the user supplies a bit more context:

```java
SearchResponse response = api.search(
    SearchInput.builder("Stripe San Francisco").build()
);
```

### Full street address

If you already have a street address, use Geocode:

```java
PlaceResults results = api.geocode(
    GeocodeInput.builder("880 Harrison St, San Francisco, CA 94107").build()
);
```

### Improve relevance with a location hint

For name-only queries, always provide a location hint when possible:

```java
SearchResponse response = api.search(
    SearchInput.builder("Stripe")
        .userLocation(UserLocation.fromLatitudeLongitude(37.7796095, -122.4016725))
        .build()
);
```

### Directions

Request routes between coordinates.

```java
DirectionsEndpoint origin = DirectionsEndpoint.fromLatitudeLongitude(53.551666, 9.9942163);
DirectionsEndpoint destination = DirectionsEndpoint.fromLatitudeLongitude(53.558, 10.0);
DirectionsResponse response = api.directions(DirectionsInput.builder(origin, destination).build());
```

### ETA

Request travel time estimates.

```java
RouteLocation origin = RouteLocation.fromLatitudeLongitude(53.551666, 9.9942163);
List<RouteLocation> destinations = List.of(RouteLocation.fromLatitudeLongitude(53.558, 10.0));
EtaResponse response = api.etas(EtaInput.builder(origin, destinations).build());
```

### Place lookup

Look up a place by its place identifier (or batch lookup).

```java
Place place = api.lookupPlace("placeId");
PlacesResponse places = api.lookupPlaces(PlaceLookupInput.builder(List.of("placeIdA", "placeIdB")).build());
```

### Alternate IDs

Resolve alternate identifiers.

```java
AlternateIdsResponse response = api.lookupAlternateIds(
    AlternateIdsInput.builder(List.of("idA", "idB")).build()
);
```

## Best practices

### Prefer Autocomplete/Search for fuzzy prompts

Geocoding is optimized for “almost complete” addresses. For fuzzy prompts, use Autocomplete or Search.

If you want “typeahead results with place details”, the SDK provides `resolveCompletionUrls`, which follows completion URLs and fetches corresponding Search responses in parallel.
Note: this can consume your quota quickly because it may trigger one Search request per completion result.

### Always provide location hints

For better result quality, include a geographic hint (for example `userLocation`) whenever possible.

## Quota notes

Apple provides per-membership daily quotas (for example, a daily service-call limit that is shared between MapKit JS service requests and Apple Maps Server API calls).
In this SDK, each call to `geocode`, `reverseGeocode`, `autocomplete`, or `search` counts as a service call (one HTTP request).
