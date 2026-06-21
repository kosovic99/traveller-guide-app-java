package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchItemResponse(
        String type,
        Long id,
        String title,
        String slug,
        @JsonProperty("country_id")
        Long countryId,
        @JsonProperty("country_name")
        String countryName,
        @JsonProperty("country_slug")
        String countrySlug,
        @JsonProperty("city_id")
        Long cityId,
        @JsonProperty("city_name")
        String cityName,
        @JsonProperty("city_slug")
        String citySlug,
        @JsonProperty("attraction_id")
        Long attractionId,
        @JsonProperty("attraction_slug")
        String attractionSlug,
        String foto,
        String description
) {

    public static SearchItemResponse fromCountry(Country country) {
        return new SearchItemResponse(
                "country",
                country.getId(),
                country.getName(),
                country.getSlug(),
                country.getId(),
                country.getName(),
                country.getSlug(),
                null,
                null,
                null,
                null,
                null,
                country.getFoto(),
                country.getDescription()
        );
    }

    public static SearchItemResponse fromCity(City city) {
        return new SearchItemResponse(
                "city",
                city.getId(),
                city.getName(),
                city.getSlug(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                city.getCountry() != null ? city.getCountry().getName() : null,
                city.getCountry() != null ? city.getCountry().getSlug() : null,
                city.getId(),
                city.getName(),
                city.getSlug(),
                null,
                null,
                city.getFoto(),
                city.getDescription()
        );
    }

    public static SearchItemResponse fromAttraction(Attraction attraction) {
        City city = attraction.getCity();
        Country country = city != null ? city.getCountry() : null;

        return new SearchItemResponse(
                "attraction",
                attraction.getId(),
                attraction.getName(),
                attraction.getSlug(),
                country != null ? country.getId() : null,
                country != null ? country.getName() : null,
                country != null ? country.getSlug() : null,
                city != null ? city.getId() : null,
                city != null ? city.getName() : null,
                city != null ? city.getSlug() : null,
                attraction.getId(),
                attraction.getSlug(),
                attraction.getFoto(),
                attraction.getDescription()
        );
    }
}
