package com.williamcallahan.applemaps.domain.request;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlternateIdsInputTest {
    private static final List<String> PLACE_IDS = List.of("place-1", "place-2");
    private static final String EXPECTED_QUERY = "?ids=place-1,place-2";

    @Test
    void toQueryStringIncludesAlternateIds() {
        AlternateIdsInput alternateIdsInput = AlternateIdsInput.builder(PLACE_IDS).build();

        assertEquals(EXPECTED_QUERY, alternateIdsInput.toQueryString());
    }

    @Test
    void buildRejectsEmptyIds() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> AlternateIdsInput.builder(List.of()).build()
        );

        assertEquals("AlternateIdsInput ids cannot be empty.", exception.getMessage());
    }
}
