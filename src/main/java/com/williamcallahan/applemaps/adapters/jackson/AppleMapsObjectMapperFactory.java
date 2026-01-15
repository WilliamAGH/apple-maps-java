package com.williamcallahan.applemaps.adapters.jackson;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.williamcallahan.applemaps.domain.model.Location;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Creates ObjectMapper instances configured for Apple Maps models.
 */
public final class AppleMapsObjectMapperFactory {

    private AppleMapsObjectMapperFactory() {}

    public static ObjectMapper create() {
        return JsonMapper.builder()
            .addMixIn(Location.class, LocationMixin.class)
            .changeDefaultPropertyInclusion(inclusion ->
                inclusion.withValueInclusion(JsonInclude.Include.NON_NULL)
            )
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
            .enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();
    }

    /**
     * Jackson mixin to map autocomplete location aliases.
     */
    abstract static class LocationMixin {

        @JsonAlias("lat")
        public abstract double latitude();

        @JsonAlias("lng")
        public abstract double longitude();
    }
}
