package com.travellerguide.traveller_guide_api.interfaces.rest.country;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.travellerguide.traveller_guide_api.domain.country.Country;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CountryResponse(
        Long id,
        String name,
        String slug,
        String foto,
        String flag,
        String description,
        Long cityCount,
        Long attractionCount
) {

    public static CountryResponse fromEntity(Country country) {
        return new CountryResponse(
                country.getId(),
                country.getName(),
                country.getSlug(),
                country.getFoto(),
                country.getFlag(),
                country.getDescription(),
                null,
                null
        );
    }

    public static CountryResponse fromEntity(Country country, long cityCount, long attractionCount) {
        return new CountryResponse(
                country.getId(),
                country.getName(),
                country.getSlug(),
                country.getFoto(),
                country.getFlag(),
                country.getDescription(),
                cityCount,
                attractionCount
        );
    }
}
