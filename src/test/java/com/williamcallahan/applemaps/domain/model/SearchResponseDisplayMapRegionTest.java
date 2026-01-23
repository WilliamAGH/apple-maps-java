package com.williamcallahan.applemaps.domain.model;

import com.williamcallahan.applemaps.adapters.jackson.AppleMapsObjectMapperFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchResponseDisplayMapRegionTest {
    private static final String JSON_WITHOUT_DISPLAY_MAP_REGION = """
        {
          "results": []
        }
        """;
    private static final String JSON_WITH_NULL_DISPLAY_MAP_REGION = """
        {
          "displayMapRegion": null,
          "results": []
        }
        """;

    @Test
    void deserializesResponseWithoutDisplayMapRegion() throws Exception {
        ObjectMapper objectMapper = AppleMapsObjectMapperFactory.create();

        SearchResponse searchResponse =
            objectMapper.readValue(JSON_WITHOUT_DISPLAY_MAP_REGION, SearchResponse.class);

        assertEquals(Optional.empty(), searchResponse.displayMapRegion());
    }

    @Test
    void deserializesResponseWithNullDisplayMapRegion() throws Exception {
        ObjectMapper objectMapper = AppleMapsObjectMapperFactory.create();

        SearchResponse searchResponse =
            objectMapper.readValue(JSON_WITH_NULL_DISPLAY_MAP_REGION, SearchResponse.class);

        assertEquals(Optional.empty(), searchResponse.displayMapRegion());
    }
}
