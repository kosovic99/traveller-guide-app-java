package com.travellerguide.traveller_guide_api.attraction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.travellerguide.traveller_guide_api.model.Attraction;
import com.travellerguide.traveller_guide_api.model.City;
import com.travellerguide.traveller_guide_api.model.Country;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AttractionResponse(
        Long id,
        Long cityId,
        String name,
        String slug,
        String description,
        String foto,
        CityInfo city
) {

    public static AttractionResponse fromEntity(Attraction attraction) {
        return new AttractionResponse(
                attraction.getId(),
                attraction.getCity() != null ? attraction.getCity().getId() : null,
                attraction.getName(),
                attraction.getSlug(),
                attraction.getDescription(),
                attraction.getFoto(),
                null
        );
    }

    public static AttractionResponse fromDetail(Attraction attraction) {
        City city = attraction.getCity();
        Country country = city != null ? city.getCountry() : null;

        CityInfo cityInfo = city == null
                ? null
                : new CityInfo(
                city.getId(),
                city.getSlug(),
                city.getName(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                country == null
                        ? null
                        : new CountryInfo(
                        country.getId(),
                        country.getSlug(),
                        country.getName()
                )
        );

        return new AttractionResponse(
                attraction.getId(),
                attraction.getCity() != null ? attraction.getCity().getId() : null,
                attraction.getName(),
                attraction.getSlug(),
                attraction.getDescription(),
                attraction.getFoto(),
                cityInfo
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CityInfo(
            Long id,
            String slug,
            String name,
            Long countryId,
            CountryInfo country
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CountryInfo(
            Long id,
            String slug,
            String name
    ) {
    }
}