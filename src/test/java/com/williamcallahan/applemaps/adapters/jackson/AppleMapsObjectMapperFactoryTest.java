package com.williamcallahan.applemaps.adapters.jackson;

import com.williamcallahan.applemaps.domain.model.SearchResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppleMapsObjectMapperFactoryTest {

    @Test
    void unknownPoiCategoryIsIgnored() throws Exception {
        ObjectMapper objectMapper = AppleMapsObjectMapperFactory.create();
        String json = """
            {
              "displayMapRegion": {
                "northLatitude": 1.0,
                "eastLongitude": 2.0,
                "southLatitude": 3.0,
                "westLongitude": 4.0
              },
              "results": [
                {
                  "id": "place-id",
                  "coordinate": { "lat": 37.7, "lng": -122.4 },
                  "displayMapRegion": {
                    "southLatitude": 37.6,
                    "westLongitude": -122.5,
                    "northLatitude": 37.8,
                    "eastLongitude": -122.3
                  },
                  "name": "Some Place",
                  "formattedAddressLines": ["Line 1"],
                  "country": "United States",
                  "countryCode": "US",
                  "poiCategory": "NewCategoryAppleAdded"
                }
              ]
            }
            """;

        SearchResponse response = objectMapper.readValue(json, SearchResponse.class);
        assertEquals(
            Optional.empty(),
            response.results().get(0).poiCategory()
        );
    }
}
