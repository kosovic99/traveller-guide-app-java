package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchItemResponse(
        String type,
        String title,
        String subtitle,
        Integer score,
        String countryName,
        String countrySlug,
        String cityName,
        String citySlug,
        String attractionSlug
) {
}
