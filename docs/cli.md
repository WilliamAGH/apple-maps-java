---
title: "CLI"
---

# CLI

This repo includes a small CLI for running Apple Maps Server queries from your terminal.

## Prerequisites

Set `APPLE_MAPS_TOKEN` (env var recommended). Optionally set `APPLE_MAPS_ORIGIN` if your token requires it (e.g. `https://api.example.com` matching your JWT's `origin` claim). See `README.md` / `docs/authorization.md`.

## Run

Via Gradle:

```bash
./gradlew cli --args='geocode "880 Harrison St, San Francisco, CA 94107"'
```

## Commands

### Geocode

```bash
./gradlew cli --args='geocode "880 Harrison St, San Francisco, CA 94107"'
./gradlew cli --args='geocode "880 Harrison St, San Francisco, CA 94107" --json'
```

### Reverse geocode

```bash
./gradlew cli --args='reverse-geocode 37.7796095 -122.4016725'
```

### Search

```bash
./gradlew cli --args='search "coffee"'
./gradlew cli --args='search "coffee" --limit-to-countries US'
```

Example: find a business / startup

```bash
./gradlew cli --args='search "Stripe"'
APPLE_MAPS_USER_LOCATION_QUERY="San Francisco, CA" ./gradlew cli --args='search "Stripe"'
```

### Autocomplete

```bash
./gradlew cli --args='autocomplete "Apple Park"'
./gradlew cli --args='autocomplete "Apple Park" --api-url'
```

### Resolve a completion URL

```bash
./gradlew cli --args='resolve "https://maps-api.apple.com/v1/search?..." --json'
```

## Location bias (better relevance)

For geocoding, search, and autocomplete, Apple supports location hints (for example `userLocation`) to bias results.

Set a default user location for the CLI via an env var:

```bash
export APPLE_MAPS_USER_LOCATION="37.7796095,-122.4016725"
./gradlew cli --args='search "coffee"'
```

`APPLE_MAPS_USER_LOCATION` is `<latitude>,<longitude>`.

If you don’t have coordinates, you can provide a natural-language hint and the CLI will geocode it once per run (this adds one extra service call):

```bash
export APPLE_MAPS_USER_LOCATION_QUERY="San Francisco, CA"
./gradlew cli --args='search "coffee"'
```

Or pass it per-command:

```bash
./gradlew cli --args='search "coffee" --user-location 37.7796095 -122.4016725'
```

## Output

By default, the CLI prints one line per result as:

`<name> — <formatted address lines>`

Notes:
- Autocomplete prints an Apple Maps web URL by default (so you can open it in a browser). Use `--api-url` to print the underlying Apple Maps Server API completion URL (for use with `resolve`).
- Use `--json` to print the raw API payload as pretty JSON.
