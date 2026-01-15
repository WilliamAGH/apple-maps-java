# Migration Task List: Apple Maps Server (Kotlin to Java 25)

## 0. Architecture & Naming Alignment
- [x] **Confirm Package Layout**: Use `com.williamcallahan.applemaps` with `domain/`, `application/`, `adapters/`, `boot` subpackages.
  - Path: `src/main/java/com/williamcallahan/applemaps`
- [ ] **Apple Maps Compatibility Contract**: Preserve Apple API type/field names (for example `results`, `AutocompleteResult`) and align models with Apple docs (Location, MapRegion, SearchMapRegion, SearchLocation/UserLocation, `Place.id`, `Place.alternateIds`, `StructuredAddress` fields, `SearchResponse.paginationInfo`, `PlacesResponse.errors`).
  - Path: `src/main/kotlin/com/doorbit/applemaps`
- [x] **Preserve Query Parameter Names**: Keep `SearchInput.q` and query-string key `q` to match Apple docs.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/request/SearchInput.java`
- [x] **Honor Longitude/Latitude Order**: Use `longitude,latitude` ordering for `SearchLocation`, `UserLocation`, and `SearchRegion` parameters.
  - Path: `src/main/java/com/williamcallahan/applemaps`
- [x] **Define Language Default Constant**: Replace inline "en-US" with a named constant.
  - Path: `src/main/java/com/williamcallahan/applemaps/AppleMaps.java`
- [x] **Eliminate Nullable Record Fields**: Use `Optional` or empty collections for `StructuredAddress` and `AutocompleteResult` fields.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/StructuredAddress.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/AutocompleteResult.java`


## 1. Project Configuration & Setup
- [x] **Update Build Script**: Configure Gradle for Java 25 (Preview), Jackson 3, and remove Kotlin plugins.
  - Path: `build.gradle.kts`
- [x] **Remove Kotlin Runtime Dependencies**: Drop `kotlin-stdlib` and `jackson-module-kotlin`.
  - Path: `build.gradle.kts`
- [x] **Create Java Source Directory**: Initialize the standard Java source set structure.
  - Path: `src/main/java/com/williamcallahan/applemaps`

## 2. Enums
- [x] **Migrate PoiCategory**: Convert `PoiCategory` enum with `apiValue` field.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/PoiCategory.java`
- [x] **Add SearchResultType**: Replace `ResultType` with Apple `SearchResultType` values.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchResultType.java`
- [x] **Add SearchACResultType**: Add Apple `SearchACResultType` values for autocomplete.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchACResultType.java`
- [x] **Add AddressCategory**: Add Apple address category values.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/AddressCategory.java`
- [x] **Add SearchRegionPriority**: Add `default` and `required` values.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchRegionPriority.java`

## 3. Simple Data Records (DTOs)
- [x] **Create Location Record**: Use Apple `Location` naming with `latitude`/`longitude` fields for response models.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/Location.java`
- [x] **Create Query Location Types**: Add `SearchLocation` and `UserLocation` value types that encode `longitude,latitude` strings.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchLocation.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/UserLocation.java`
- [x] **Create Map Region Records**: Use Apple `MapRegion` and `SearchMapRegion` naming with `northLatitude`, `southLatitude`, `eastLongitude`, `westLongitude` fields.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/MapRegion.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchMapRegion.java`
- [x] **Create SearchRegion Query Type**: Encode `northLatitude,eastLongitude,southLatitude,westLongitude` for search parameters.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchRegion.java`
- [x] **Migrate StructuredAddress**: Convert to Java Record and include Apple fields (`administrativeArea`, `administrativeAreaCode`, `areasOfInterest`, `subAdministrativeArea`).
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/StructuredAddress.java`
- [x] **Migrate Place**: Convert to Java Record and include Apple fields (`id`, `alternateIds`).
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/Place.java`
- [x] **Split Place Response Types**: Move `PlaceResults`, `PlacesResponse`, `Place`, `MapRegion`, `StructuredAddress` into separate Java files.
  - Path: `src/main/kotlin/com/doorbit/applemaps/PlaceResponseModel.kt`

## 4. Input Models (Records + Builders)
- [x] **Migrate GeocodeInput**: Create Record with a static inner `Builder` class for optional parameters. Implement `toQueryString()` with Apple parameter names.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/request/GeocodeInput.java`
- [x] **Migrate SearchInput**: Create Record with a static inner `Builder` class. Implement `toQueryString()` with Apple parameter names and coordinate order.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/request/SearchInput.java`
- [x] **Add SearchAutocompleteInput**: Create Record with a static inner `Builder` class for autocomplete parameters.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/request/SearchAutocompleteInput.java`

## 5. Response Models
- [x] **Migrate PlaceResults**: Add Apple `PlaceResults` for geocode/reverse geocode responses.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/PlaceResults.java`
- [x] **Migrate PlacesResponse**: Convert to Java Record and include Apple `errors` with typed `PlaceLookupError` entries.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/PlacesResponse.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/PlaceLookupError.java`
- [x] **Migrate SearchResponse**: Convert to Java Record and include Apple `paginationInfo` and `SearchResponse.Place`.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchResponse.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchResponsePlace.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/PaginationInfo.java`
- [x] **Migrate SearchAutocompleteResponse**: Convert to Java Record.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/SearchAutocompleteResponse.java`
- [x] **Migrate AutocompleteResult**: Convert to Java Record (needed for `SearchAutocompleteResponse`) and align fields with Apple docs, including `location` field naming (`lat`/`lng` vs `latitude`/`longitude`).
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/AutocompleteResult.java`
- [x] **Split Autocomplete Response Types**: Separate `SearchAutocompleteResponse` and `AutocompleteResult` into individual Java files.
  - Path: `src/main/kotlin/com/doorbit/applemaps/SearchAutocompleteResponseModel.kt`
- [x] **Add TokenResponse + ErrorResponse**: Introduce canonical response types for auth/errors.
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/TokenResponse.java`
  - Path: `src/main/java/com/williamcallahan/applemaps/domain/model/ErrorResponse.java`

## 6. Core Logic & Services
- [x] **Migrate Authorization Service**: Rewrite using `AtomicReference` or `ReentrantLock` for thread-safe token management. Use standard `HttpClient`.
  - Path: `src/main/java/com/williamcallahan/applemaps/adapters/mapsserver/AppleMapsAuthorizationService.java`
- [x] **Fix JWT Parsing and Expiry Extraction**: Avoid regex-based split on `.` and validate token structure before decoding.
  - Path: `src/main/java/com/williamcallahan/applemaps/adapters/mapsserver/AppleMapsAuthorizationService.java`
- [x] **Add Jackson Mixins**: Map Apple JSON aliases (for example `lat`/`lng` on `Location`) without polluting domain models.
  - Path: `src/main/java/com/williamcallahan/applemaps/adapters/jackson`
- [x] **Migrate AppleMaps Client**: Rewrite main client.
  - Use `java.net.http.HttpClient` with `newVirtualThreadPerTaskExecutor`.
  - Implement `geocode`, `search`, `reverseGeocode`, and `autocomplete`.
  - Add completion URL resolver using `StructuredTaskScope` (Preview).
  - Verify endpoint casing against Apple docs (`searchAutocomplete` vs `searchautocomplete`).
  - Path: `src/main/java/com/williamcallahan/applemaps/AppleMaps.java`
- [x] **Improve Error Context**: Replace generic "Geocoding failed" exception text with operation-specific messaging.
  - Path: `src/main/java/com/williamcallahan/applemaps/AppleMaps.java`

## 7. Cleanup & Verification
- [x] **Add Query String Tests**: Cover `GeocodeInput` and `SearchInput` query encoding and defaults.
  - Path: `src/test/java/com/williamcallahan/applemaps/domain/request`
- [x] **Add Authorization Tests**: Validate access token expiry parsing and refresh behavior.
  - Path: `src/test/java/com/williamcallahan/applemaps/adapters/mapsserver`
- [x] **Run Gradle Tests**: `./gradlew test`.
- [x] **Run Gradle Build**: `./gradlew build`.
- [ ] **Run Spotless Check**: `./gradlew spotlessCheck` (if configured).
- [ ] **Remove Kotlin Source**: Delete the legacy Kotlin source directory.
  - Path: `src/main/kotlin`

## 8. File Migration Map (From > To + Tasks)

### Root & Documentation
- [ ] **`AGENTS.md` > `AGENTS.md`**: No migration changes; enforce [FS1/ND1/NO1/AR1] during edits.
- [ ] **`.gitignore` > `.gitignore`**: Confirm Java build outputs remain ignored; add new ignores if needed.
- [ ] **`LICENSE` > `LICENSE`**: No changes required.
- [x] **`README.md` > `README.md`**: Update Kotlin references, dependency coordinates, and examples to Java.
- [ ] **`docs/tasks/2026-01-14-migrate-to-java-tasklist.md` > same**: Maintain checklist + file mapping.

### Gradle & Wrapper
- [x] **`settings.gradle.kts` > `settings.gradle.kts`**: Update `rootProject.name` and module naming if artifact ID changes.
- [x] **`gradle.properties` > `gradle.properties`**: Rename POM fields for Java artifact, update description/URLs.
- [ ] **`gradle/wrapper/gradle-wrapper.properties` > same**: Bump Gradle version if required for Java 25 preview.
- [ ] **`gradle/wrapper/gradle-wrapper.jar` > same**: Regenerate wrapper JAR when version changes.
- [ ] **`gradlew` > `gradlew`**: Regenerate if wrapper is updated.
- [ ] **`gradlew.bat` > `gradlew.bat`**: Regenerate if wrapper is updated.
- [x] **`build.gradle.kts` > same**: Remove Kotlin plugin/deps, add Java 25 toolchain + preview args, keep Jackson 3.

### Kotlin Sources > Java Sources
- [x] **`src/main/kotlin/com/doorbit/applemaps/AppleMaps.kt` > `src/main/java/com/williamcallahan/applemaps/AppleMaps.java`**: Use virtual thread executor in `HttpAppleMapsGateway`, `StructuredTaskScope` for completion URL resolution, named language constant, and operation-specific errors.
- [x] **`src/main/kotlin/com/doorbit/applemaps/AppleMapsAuthorizationService.kt` > `src/main/java/com/williamcallahan/applemaps/adapters/mapsserver/AppleMapsAuthorizationService.java`**: Thread-safe token cache, safe JWT parsing (no regex split), avoid nullable token storage.
- [x] **`src/main/kotlin/com/doorbit/applemaps/BoundingBox.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchRegion.java`**: Align naming to Apple `SearchRegion` query type and longitude/latitude ordering.
- [x] **`src/main/kotlin/com/doorbit/applemaps/Coordinate.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/Location.java`**: Align naming to Apple `Location` (`latitude`, `longitude`).
- [x] **`src/main/kotlin/com/doorbit/applemaps/GeocodeInput.kt` > `src/main/java/com/williamcallahan/applemaps/domain/request/GeocodeInput.java`**: Record + builder, encoded query params, no null fields.
- [x] **`src/main/kotlin/com/doorbit/applemaps/LatLon.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchLocation.java`**: Align naming to Apple `SearchLocation`/`UserLocation` query types with `longitude,latitude` order.
- [x] **`src/main/kotlin/com/doorbit/applemaps/PlaceResponseModel.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/PlaceResults.java`, `PlacesResponse.java`, `Place.java`, `MapRegion.java`, `StructuredAddress.java`**: Split file, remove nullable fields via `Optional`/empty lists, add Apple fields.
- [x] **`src/main/kotlin/com/doorbit/applemaps/PoiCategory.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/PoiCategory.java`**: Enum with `apiValue`.
- [x] **`src/main/kotlin/com/doorbit/applemaps/ResultType.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchResultType.java`**: Rename to Apple `SearchResultType` values.
- [x] **`new` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchACResultType.java`**: Add Apple autocomplete result types.
- [x] **`new` > `src/main/java/com/williamcallahan/applemaps/domain/model/AddressCategory.java`**: Add Apple address categories.
- [x] **`new` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchRegionPriority.java`**: Add search region priority values.
- [x] **`src/main/kotlin/com/doorbit/applemaps/SearchAutocompleteResponseModel.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchAutocompleteResponse.java`, `AutocompleteResult.java`**: Split file, remove null fields, preserve `withPlaces` behavior, add Apple fields.
- [x] **`src/main/kotlin/com/doorbit/applemaps/SearchInput.kt` > `src/main/java/com/williamcallahan/applemaps/domain/request/SearchInput.java`**: Preserve `q` field, record + builder, encoded query params, no null fields.
- [x] **`src/main/kotlin/com/doorbit/applemaps/SearchResponseModel.kt` > `src/main/java/com/williamcallahan/applemaps/domain/model/SearchResponse.java`, `SearchResponsePlace.java`**: Record with empty list defaults and `paginationInfo`.

### Tests (New Files)
- [x] **`new` > `src/test/java/com/williamcallahan/applemaps/GeocodeInputTest.java`**: Query string encoding and defaults.
- [x] **`new` > `src/test/java/com/williamcallahan/applemaps/SearchInputTest.java`**: Query string encoding and `q` field mapping.
- [x] **`new` > `src/test/java/com/williamcallahan/applemaps/AppleMapsAuthorizationServiceTest.java`**: Expiry parsing + refresh behavior.

### Cleanup
- [x] **`src/main/kotlin/**` > removed**: Delete Kotlin sources after Java migration.
- [ ] **`apple-maps-server-kotlin/**` > removed**: Remove legacy nested module directory after root migration is complete.
