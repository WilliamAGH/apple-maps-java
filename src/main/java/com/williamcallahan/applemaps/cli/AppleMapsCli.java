package com.williamcallahan.applemaps.cli;

import com.williamcallahan.applemaps.AppleMaps;
import com.williamcallahan.applemaps.adapters.jackson.AppleMapsObjectMapperFactory;
import com.williamcallahan.applemaps.adapters.mapsserver.AppleMapsApiException;
import com.williamcallahan.applemaps.domain.model.AutocompleteResult;
import com.williamcallahan.applemaps.domain.model.Place;
import com.williamcallahan.applemaps.domain.model.PlaceResults;
import com.williamcallahan.applemaps.domain.model.SearchAutocompleteResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponse;
import com.williamcallahan.applemaps.domain.model.SearchResponsePlace;
import com.williamcallahan.applemaps.domain.model.UserLocation;
import com.williamcallahan.applemaps.domain.request.GeocodeInput;
import com.williamcallahan.applemaps.domain.request.SearchAutocompleteInput;
import com.williamcallahan.applemaps.domain.request.SearchInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class AppleMapsCli {

    private static final int EXIT_USAGE = 2;
    private static final int EXIT_FAILURE = 1;
    private static final String API_SERVER = "https://maps-api.apple.com";
    private static final String APPLE_MAPS_WEB_SERVER =
        "https://maps.apple.com";
    private static final String APPLE_MAPS_WEB_QUERY_PARAMETER = "q";
    private static final String COMMAND_GEOCODE = "geocode";
    private static final String COMMAND_REVERSE_GEOCODE = "reverse-geocode";
    private static final String COMMAND_SEARCH = "search";
    private static final String COMMAND_AUTOCOMPLETE = "autocomplete";
    private static final String COMMAND_RESOLVE = "resolve";
    private static final String FLAG_JSON = "--json";
    private static final String FLAG_API_URL = "--api-url";
    private static final String FLAG_LANGUAGE = "--lang";
    private static final String FLAG_USER_LOCATION = "--user-location";
    private static final String FLAG_LIMIT_TO_COUNTRIES =
        "--limit-to-countries";
    private static final String FLAG_HELP = "--help";
    private static final String SHORT_FLAG_HELP = "-h";
    private static final String DEFAULT_LANGUAGE = "en-US";
    private static final String ADDRESS_JOINER = ", ";
    private static final String OUTPUT_SEPARATOR = " — ";
    private static final String COUNTRY_SEPARATOR = ",";
    private static final String USER_LOCATION_SEPARATOR = ",";
    private static final String ENVIRONMENT_USER_LOCATION =
        "APPLE_MAPS_USER_LOCATION";
    private static final String ENVIRONMENT_USER_LOCATION_QUERY =
        "APPLE_MAPS_USER_LOCATION_QUERY";
    private static final String URL_QUERY_SEPARATOR = "?";
    private static final String URL_PARAMETER_SEPARATOR = "&";
    private static final String URL_NAME_VALUE_SEPARATOR = "=";

    private AppleMapsCli() {}

    public static void main(String[] args) {
        try {
            run(args);
        } catch (UsageException exception) {
            System.err.println(exception.getMessage());
            System.err.println();
            printUsage();
            System.exit(EXIT_USAGE);
        } catch (Exception exception) {
            System.err.println("Error: " + safeMessage(exception));
            Throwable cause = exception.getCause();
            if (cause != null) {
                System.err.println(
                    "Cause: " +
                        cause.getClass().getName() +
                        ": " +
                        safeMessage(cause)
                );
            }
            if (exception instanceof AppleMapsApiException apiException) {
                String responseBody = apiException.responseBody();
                if (responseBody != null && !responseBody.isBlank()) {
                    System.err.println("Response: " + responseBody);
                }
            }
            System.exit(EXIT_FAILURE);
        }
    }

    private static String safeMessage(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return throwable.getClass().getName();
        }
        return message;
    }

    private static void run(String[] args) {
        Objects.requireNonNull(args, "args");
        if (args.length == 0 || isHelpFlag(args[0])) {
            printUsage();
            return;
        }

        String command = args[0];
        ParsedOptions options = ParsedOptions.parse(
            Arrays.copyOfRange(args, 1, args.length)
        );
        String token = resolveToken();

        try (AppleMaps api = new AppleMaps(token)) {
            ParsedOptions resolvedOptions = commandUsesLocationHints(command)
                ? options.resolveUserLocation(api)
                : options;
            switch (command) {
                case COMMAND_GEOCODE -> geocode(api, resolvedOptions);
                case COMMAND_REVERSE_GEOCODE -> reverseGeocode(
                    api,
                    resolvedOptions
                );
                case COMMAND_SEARCH -> search(api, resolvedOptions);
                case COMMAND_AUTOCOMPLETE -> autocomplete(api, resolvedOptions);
                case COMMAND_RESOLVE -> resolveCompletionUrl(
                    api,
                    resolvedOptions
                );
                default -> throw new UsageException(
                    "Unknown command: " + command
                );
            }
        }
    }

    private static boolean commandUsesLocationHints(String command) {
        return (
            COMMAND_GEOCODE.equals(command) ||
            COMMAND_SEARCH.equals(command) ||
            COMMAND_AUTOCOMPLETE.equals(command)
        );
    }

    private static void geocode(AppleMaps api, ParsedOptions options) {
        String address = options.requiredJoinedArgument(
            COMMAND_GEOCODE + " <address>"
        );
        GeocodeInput.Builder inputBuilder = GeocodeInput.builder(address);
        options.language().ifPresent(inputBuilder::language);
        options.userLocation().ifPresent(inputBuilder::userLocation);
        if (!options.limitToCountries().isEmpty()) {
            inputBuilder.limitToCountries(options.limitToCountries());
        }
        PlaceResults geocodeResults = api.geocode(inputBuilder.build());
        if (options.json()) {
            printJson(geocodeResults);
            return;
        }
        printPlaces(geocodeResults.results());
    }

    private static void reverseGeocode(AppleMaps api, ParsedOptions options) {
        List<String> args = options.remainingArgs();
        if (args.size() < 2) {
            throw new UsageException(
                "Usage: reverse-geocode <latitude> <longitude> [--lang <bcp47>] [--json]"
            );
        }
        double latitude = parseDouble(args.get(0), "latitude");
        double longitude = parseDouble(args.get(1), "longitude");
        String language = options.language().orElse(DEFAULT_LANGUAGE);
        PlaceResults reverseGeocodeResults = api.reverseGeocode(
            latitude,
            longitude,
            language
        );
        if (options.json()) {
            printJson(reverseGeocodeResults);
            return;
        }
        printPlaces(reverseGeocodeResults.results());
    }

    private static void search(AppleMaps api, ParsedOptions options) {
        String query = options.requiredJoinedArgument(
            COMMAND_SEARCH + " <query>"
        );
        SearchInput.Builder inputBuilder = SearchInput.builder(query);
        options.language().ifPresent(inputBuilder::language);
        options.userLocation().ifPresent(inputBuilder::userLocation);
        if (!options.limitToCountries().isEmpty()) {
            inputBuilder.limitToCountries(options.limitToCountries());
        }
        SearchResponse searchResponse = api.search(inputBuilder.build());
        if (options.json()) {
            printJson(searchResponse);
            return;
        }
        printSearchPlaces(searchResponse.results());
    }

    private static void autocomplete(AppleMaps api, ParsedOptions options) {
        String query = options.requiredJoinedArgument(
            COMMAND_AUTOCOMPLETE + " <query>"
        );
        SearchAutocompleteInput.Builder inputBuilder =
            SearchAutocompleteInput.builder(query);
        options.language().ifPresent(inputBuilder::language);
        options.userLocation().ifPresent(inputBuilder::userLocation);
        if (!options.limitToCountries().isEmpty()) {
            inputBuilder.limitToCountries(options.limitToCountries());
        }
        SearchAutocompleteResponse autocompleteResponse = api.autocomplete(
            inputBuilder.build()
        );
        if (options.json()) {
            printJson(autocompleteResponse);
            return;
        }
        printAutocompleteResults(
            autocompleteResponse.results(),
            options.apiUrl()
        );
    }

    private static void resolveCompletionUrl(
        AppleMaps api,
        ParsedOptions options
    ) {
        List<String> args = options.remainingArgs();
        if (args.isEmpty()) {
            throw new UsageException("Usage: resolve <completionUrl> [--json]");
        }
        String completionUrl = normalizeCompletionUrl(joinWithSpace(args));
        SearchResponse searchResponse = api.resolveCompletionUrl(completionUrl);
        if (options.json()) {
            printJson(searchResponse);
            return;
        }
        printSearchPlaces(searchResponse.results());
    }

    private static String resolveToken() {
        java.util.Optional<String> tokenText = readSettingText(
            "APPLE_MAPS_TOKEN"
        );
        if (tokenText.isPresent()) {
            return tokenText.orElseThrow();
        }
        throw new UsageException(
            "Missing APPLE_MAPS_TOKEN. Set it as an env var or JVM system property."
        );
    }

    private static java.util.Optional<String> readSettingText(
        String settingName
    ) {
        Objects.requireNonNull(settingName, "settingName");
        String environmentValue = System.getenv(settingName);
        if (environmentValue != null) {
            String trimmedEnvironmentValue = environmentValue.trim();
            if (!trimmedEnvironmentValue.isBlank()) {
                return java.util.Optional.of(trimmedEnvironmentValue);
            }
        }

        String systemPropertyValue = System.getProperty(settingName);
        if (systemPropertyValue != null) {
            String trimmedSystemPropertyValue = systemPropertyValue.trim();
            if (!trimmedSystemPropertyValue.isBlank()) {
                return java.util.Optional.of(trimmedSystemPropertyValue);
            }
        }

        return java.util.Optional.empty();
    }

    private static void printPlaces(List<Place> places) {
        for (Place place : places) {
            String address = formatAddressLines(place.formattedAddressLines());
            System.out.println(place.name() + OUTPUT_SEPARATOR + address);
        }
    }

    private static void printSearchPlaces(List<SearchResponsePlace> places) {
        for (SearchResponsePlace place : places) {
            String address = formatAddressLines(place.formattedAddressLines());
            System.out.println(place.name() + OUTPUT_SEPARATOR + address);
        }
    }

    private static void printAutocompleteResults(
        List<AutocompleteResult> autocompleteResults,
        boolean apiUrl
    ) {
        for (AutocompleteResult autocompleteResult : autocompleteResults) {
            String display = String.join(
                ADDRESS_JOINER,
                autocompleteResult.displayLines()
            );
            String completionUrl = autocompleteResult.completionUrl();
            String outputUrl = apiUrl
                ? toApiUrl(completionUrl)
                : toAppleMapsWebUrl(completionUrl).orElseGet(() ->
                      toApiUrl(completionUrl)
                  );
            System.out.println(display + OUTPUT_SEPARATOR + outputUrl);
        }
    }

    private static String formatAddressLines(List<String> addressLines) {
        return String.join(ADDRESS_JOINER, addressLines);
    }

    private static void printJson(Object payload) {
        try {
            String json = AppleMapsObjectMapperFactory.create()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(payload);
            System.out.println(json);
        } catch (Exception exception) {
            throw new RuntimeException(
                "Failed to serialize response as JSON",
                exception
            );
        }
    }

    private static double parseDouble(String rawValue, String label) {
        try {
            return Double.parseDouble(rawValue);
        } catch (NumberFormatException exception) {
            throw new UsageException("Invalid " + label + ": " + rawValue);
        }
    }

    private static boolean isHelpFlag(String arg) {
        return FLAG_HELP.equals(arg) || SHORT_FLAG_HELP.equals(arg);
    }

    static String toApiUrl(String completionUrl) {
        String normalizedCompletionUrl = normalizeCompletionUrl(completionUrl);
        return API_SERVER + normalizedCompletionUrl;
    }

    static java.util.Optional<String> toAppleMapsWebUrl(String completionUrl) {
        String normalizedCompletionUrl = normalizeCompletionUrl(completionUrl);
        java.util.Optional<String> query = extractQueryParameter(
            normalizedCompletionUrl,
            APPLE_MAPS_WEB_QUERY_PARAMETER
        );
        return query.map(
            queryValue -> APPLE_MAPS_WEB_SERVER + "/?q=" + queryValue
        );
    }

    static java.util.Optional<UserLocation> parseUserLocation(
        String userLocationText
    ) {
        if (userLocationText == null || userLocationText.isBlank()) {
            return java.util.Optional.empty();
        }
        String[] parts = userLocationText.trim().split(USER_LOCATION_SEPARATOR);
        if (parts.length != 2) {
            throw new UsageException(
                "Invalid " +
                    ENVIRONMENT_USER_LOCATION +
                    ". Expected \"<latitude>,<longitude>\"."
            );
        }
        double latitude = parseDouble(parts[0].trim(), "latitude");
        double longitude = parseDouble(parts[1].trim(), "longitude");
        return java.util.Optional.of(
            UserLocation.fromLatitudeLongitude(latitude, longitude)
        );
    }

    static UserLocation geocodeFirstResultToUserLocation(
        AppleMaps api,
        GeocodeInput geocodeInput
    ) {
        Objects.requireNonNull(api, "api");
        Objects.requireNonNull(geocodeInput, "geocodeInput");
        PlaceResults geocodeResults = api.geocode(geocodeInput);
        if (geocodeResults.results().isEmpty()) {
            throw new UsageException(
                "Unable to resolve " +
                    ENVIRONMENT_USER_LOCATION_QUERY +
                    " to coordinates (no geocode results)."
            );
        }
        Place firstPlace = geocodeResults.results().get(0);
        return UserLocation.fromLatitudeLongitude(
            firstPlace.coordinate().latitude(),
            firstPlace.coordinate().longitude()
        );
    }

    private static String normalizeCompletionUrl(String completionUrl) {
        Objects.requireNonNull(completionUrl, "completionUrl");
        String trimmed = completionUrl.trim();
        if (trimmed.startsWith(API_SERVER)) {
            return trimmed.substring(API_SERVER.length());
        }
        return trimmed;
    }

    private static java.util.Optional<String> extractQueryParameter(
        String url,
        String parameterName
    ) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(parameterName, "parameterName");
        int queryIndex = url.indexOf(URL_QUERY_SEPARATOR);
        if (queryIndex < 0 || queryIndex == url.length() - 1) {
            return java.util.Optional.empty();
        }
        String queryText = url.substring(queryIndex + 1);
        String[] parameters = queryText.split(URL_PARAMETER_SEPARATOR);
        for (String parameter : parameters) {
            int delimiter = parameter.indexOf(URL_NAME_VALUE_SEPARATOR);
            if (delimiter <= 0) {
                continue;
            }
            String name = parameter.substring(0, delimiter);
            if (!parameterName.equals(name)) {
                continue;
            }
            String parameterValue = parameter.substring(delimiter + 1);
            if (parameterValue.isBlank()) {
                return java.util.Optional.empty();
            }
            return java.util.Optional.of(parameterValue);
        }
        return java.util.Optional.empty();
    }

    private static void printUsage() {
        String usage = """
            Apple Maps CLI (uses APPLE_MAPS_TOKEN)

            Usage:
              geocode <address> [--lang <bcp47>] [--user-location <lat> <lon>] [--limit-to-countries <US,CA>] [--json]
              reverse-geocode <latitude> <longitude> [--lang <bcp47>] [--json]
              search <query> [--lang <bcp47>] [--user-location <lat> <lon>] [--limit-to-countries <US,CA>] [--json]
              autocomplete <query> [--lang <bcp47>] [--user-location <lat> <lon>] [--limit-to-countries <US,CA>] [--api-url] [--json]
              resolve <completionUrl> [--json]

            Examples:
              ./gradlew cli --args='geocode "880 Harrison St, San Francisco, CA 94107"'
              ./gradlew cli --args='reverse-geocode 37.7796095 -122.4016725'
              ./gradlew cli --args='autocomplete "Apple Park"'
              APPLE_MAPS_USER_LOCATION='37.7796095,-122.4016725' ./gradlew cli --args='search "coffee"'
              APPLE_MAPS_USER_LOCATION_QUERY='San Francisco, CA' ./gradlew cli --args='search "coffee"'
            """;
        System.out.println(usage);
    }

    private record ParsedOptions(
        boolean json,
        boolean apiUrl,
        List<String> remainingArgs,
        java.util.Optional<String> language,
        java.util.Optional<UserLocation> userLocation,
        List<String> limitToCountries
    ) {
        static ParsedOptions parse(String[] rawArgs) {
            boolean json = false;
            boolean apiUrl = false;
            java.util.Optional<String> language = java.util.Optional.empty();
            java.util.Optional<UserLocation> userLocation =
                java.util.Optional.empty();
            List<String> limitToCountries = List.of();
            List<String> remainingArgs = new ArrayList<>();

            for (int index = 0; index < rawArgs.length; index++) {
                String arg = Objects.requireNonNull(
                    rawArgs[index],
                    "arg"
                ).trim();
                if (arg.isBlank()) {
                    continue;
                }
                switch (arg) {
                    case FLAG_JSON -> json = true;
                    case FLAG_API_URL -> apiUrl = true;
                    case FLAG_LANGUAGE -> {
                        language = java.util.Optional.of(
                            requireNextValue(rawArgs, ++index, FLAG_LANGUAGE)
                        );
                    }
                    case FLAG_USER_LOCATION -> {
                        String latitudeText = requireNextValue(
                            rawArgs,
                            ++index,
                            FLAG_USER_LOCATION
                        );
                        String longitudeText = requireNextValue(
                            rawArgs,
                            ++index,
                            FLAG_USER_LOCATION
                        );
                        double latitude = parseDouble(latitudeText, "latitude");
                        double longitude = parseDouble(
                            longitudeText,
                            "longitude"
                        );
                        userLocation = java.util.Optional.of(
                            UserLocation.fromLatitudeLongitude(
                                latitude,
                                longitude
                            )
                        );
                    }
                    case FLAG_LIMIT_TO_COUNTRIES -> {
                        String countries = requireNextValue(
                            rawArgs,
                            ++index,
                            FLAG_LIMIT_TO_COUNTRIES
                        );
                        limitToCountries = parseCountries(countries);
                    }
                    default -> remainingArgs.add(arg);
                }
            }

            if (userLocation.isEmpty()) {
                userLocation = parseUserLocation(
                    readSettingText(ENVIRONMENT_USER_LOCATION).orElse(null)
                );
            }

            return new ParsedOptions(
                json,
                apiUrl,
                List.copyOf(remainingArgs),
                language,
                userLocation,
                limitToCountries
            );
        }

        ParsedOptions resolveUserLocation(AppleMaps api) {
            Objects.requireNonNull(api, "api");
            if (userLocation.isPresent()) {
                return this;
            }

            java.util.Optional<String> userLocationQuery = readSettingText(
                ENVIRONMENT_USER_LOCATION_QUERY
            );
            if (userLocationQuery.isEmpty()) {
                return this;
            }

            GeocodeInput.Builder inputBuilder = GeocodeInput.builder(
                userLocationQuery.orElseThrow()
            );
            language.ifPresent(inputBuilder::language);
            if (!limitToCountries.isEmpty()) {
                inputBuilder.limitToCountries(limitToCountries);
            }

            UserLocation resolvedUserLocation =
                geocodeFirstResultToUserLocation(api, inputBuilder.build());
            return new ParsedOptions(
                json,
                apiUrl,
                remainingArgs,
                language,
                java.util.Optional.of(resolvedUserLocation),
                limitToCountries
            );
        }

        String requiredJoinedArgument(String usageHint) {
            if (remainingArgs.isEmpty()) {
                throw new UsageException("Usage: " + usageHint);
            }
            return joinWithSpace(remainingArgs);
        }

        private static String requireNextValue(
            String[] rawArgs,
            int index,
            String flagName
        ) {
            if (index < 0 || index >= rawArgs.length) {
                throw new UsageException("Missing value for " + flagName);
            }
            String flagArgument = Objects.requireNonNull(
                rawArgs[index],
                flagName
            ).trim();
            if (flagArgument.isBlank()) {
                throw new UsageException("Missing value for " + flagName);
            }
            return flagArgument;
        }

        private static List<String> parseCountries(String rawValue) {
            String trimmed = Objects.requireNonNull(
                rawValue,
                "rawValue"
            ).trim();
            if (trimmed.isBlank()) {
                return List.of();
            }
            String[] parts = trimmed.split(COUNTRY_SEPARATOR);
            List<String> countries = Arrays.stream(parts)
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .map(part -> part.toUpperCase(Locale.ROOT))
                .toList();
            return List.copyOf(countries);
        }
    }

    private static String joinWithSpace(List<String> parts) {
        return parts
            .stream()
            .reduce((left, right) -> left + " " + right)
            .orElse("");
    }

    private static final class UsageException extends RuntimeException {

        private UsageException(String message) {
            super(message);
        }
    }
}
