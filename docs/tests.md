---
title: "Tests"
---

# Tests

This repo has unit tests and an optional integration test that calls the live Apple Maps Server API.

## Unit tests

```bash
./gradlew test
```

## Integration test (live API)

The integration test class ends with `IT` and is enabled only when `APPLE_MAPS_TOKEN` is set.

Option A: use `.env` (local)

```bash
cp .env-example .env
```

Then put your token into `.env` as `APPLE_MAPS_TOKEN=...` and run:

```bash
./gradlew testDetail
```

To run a single “smoke test”:

```bash
./gradlew testDetail --tests com.williamcallahan.applemaps.AppleMapsIT.geocodeHandlesPartialAndFullAddresses
```

Option B: environment variable (CI/local)

```bash
APPLE_MAPS_TOKEN="your-token" ./gradlew testDetail
```
