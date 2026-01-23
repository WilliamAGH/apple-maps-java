package com.williamcallahan.applemaps.domain.model;

import com.williamcallahan.applemaps.adapters.jackson.AppleMapsObjectMapperFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceLookupErrorTest {
    private static final String JSON_WITH_ID = """
        {
          "errorCode": "FAILED_INVALID_ID",
          "id": "invalid-place-id"
        }
        """;
    private static final String JSON_WITHOUT_ID = """
        {
          "errorCode": "FAILED_INTERNAL_ERROR"
        }
        """;
    private static final String JSON_WITH_NULL_ID = """
        {
          "errorCode": "FAILED_INTERNAL_ERROR",
          "id": null
        }
        """;

    @Test
    void deserializesErrorWithId() throws Exception {
        ObjectMapper objectMapper = AppleMapsObjectMapperFactory.create();

        PlaceLookupError placeLookupError =
            objectMapper.readValue(JSON_WITH_ID, PlaceLookupError.class);

        assertEquals(PlaceLookupErrorCode.FAILED_INVALID_ID, placeLookupError.errorCode());
        assertEquals(Optional.of("invalid-place-id"), placeLookupError.id());
    }

    @Test
    void deserializesErrorWithoutId() throws Exception {
        ObjectMapper objectMapper = AppleMapsObjectMapperFactory.create();

        PlaceLookupError placeLookupError =
            objectMapper.readValue(JSON_WITHOUT_ID, PlaceLookupError.class);

        assertEquals(PlaceLookupErrorCode.FAILED_INTERNAL_ERROR, placeLookupError.errorCode());
        assertEquals(Optional.empty(), placeLookupError.id());
    }

    @Test
    void deserializesErrorWithNullId() throws Exception {
        ObjectMapper objectMapper = AppleMapsObjectMapperFactory.create();

        PlaceLookupError placeLookupError =
            objectMapper.readValue(JSON_WITH_NULL_ID, PlaceLookupError.class);

        assertEquals(PlaceLookupErrorCode.FAILED_INTERNAL_ERROR, placeLookupError.errorCode());
        assertEquals(Optional.empty(), placeLookupError.id());
    }
}
