package com.travellerguide.traveller_guide_api.interfaces.rest.city;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CityResponse(
        Long id,
        Long countryId,
        String name,
        String slug,
        String foto,
        String rating,
        String description,
        Long attractionCount,
        CountryInfo country,
        List<AttractionItem> attractions
) {

    public static CityResponse fromEntity(City city) {
        return new CityResponse(
                city.getId(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                city.getName(),
                city.getSlug(),
                city.getFoto(),
                city.getRating(),
                city.getDescription(),
                null,
                null,
                null
        );
    }

    public static CityResponse fromEntity(City city, long attractionCount) {
        return new CityResponse(
                city.getId(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                city.getName(),
                city.getSlug(),
                city.getFoto(),
                city.getRating(),
                city.getDescription(),
                attractionCount,
                null,
                null
        );
    }

    public static CityResponse fromCityWithAttractions(City city, List<Attraction> attractions) {
        List<AttractionItem> attractionItems = attractions == null
                ? List.of()
                : attractions.stream()
                .map(AttractionItem::fromEntity)
                .toList();

        CountryInfo countryInfo = city.getCountry() == null
                ? null
                : new CountryInfo(
                city.getCountry().getId(),
                city.getCountry().getSlug(),
                city.getCountry().getName()
        );

        return new CityResponse(
                city.getId(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                city.getName(),
                city.getSlug(),
                city.getFoto(),
                city.getRating(),
                city.getDescription(),
                null,
                countryInfo,
                attractionItems
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CountryInfo(
            Long id,
            String slug,
            String name
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AttractionItem(
            Long id,
            Long cityId,
            String name,
            String slug,
            String description,
            String foto
    ) {
        public static AttractionItem fromEntity(Attraction attraction) {
            return new AttractionItem(
                    attraction.getId(),
                    attraction.getCity() != null ? attraction.getCity().getId() : null,
                    attraction.getName(),
                    attraction.getSlug(),
                    attraction.getDescription(),
                    attraction.getFoto()
            );
        }
    }
}
