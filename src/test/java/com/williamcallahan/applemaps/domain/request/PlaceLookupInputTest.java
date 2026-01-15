package com.williamcallahan.applemaps.domain.request;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlaceLookupInputTest {
    private static final List<String> PLACE_IDS = List.of("place-1", "place-2");
    private static final String LANGUAGE = "en-US";
    private static final String EXPECTED_QUERY = "?ids=place-1,place-2&lang=en-US";

    @Test
    void toQueryStringIncludesLookupParameters() {
        PlaceLookupInput placeLookupInput = PlaceLookupInput.builder(PLACE_IDS)
            .language(LANGUAGE)
            .build();

        assertEquals(EXPECTED_QUERY, placeLookupInput.toQueryString());
    }

    @Test
    void buildRejectsEmptyIds() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PlaceLookupInput.builder(List.of()).build()
        );

        assertEquals("PlaceLookupInput ids cannot be empty.", exception.getMessage());
    }
}
