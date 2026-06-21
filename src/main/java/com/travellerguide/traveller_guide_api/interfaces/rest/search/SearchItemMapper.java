package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SearchItemMapper {

    @Mapping(target = "type", constant = "country")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "countryId", source = "id")
    @Mapping(target = "countryName", source = "name")
    @Mapping(target = "countrySlug", source = "slug")
    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "cityName", ignore = true)
    @Mapping(target = "citySlug", ignore = true)
    @Mapping(target = "attractionId", ignore = true)
    @Mapping(target = "attractionSlug", ignore = true)
    SearchItemResponse fromCountry(Country country);

    @Mapping(target = "type", constant = "city")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "countryName", source = "country.name")
    @Mapping(target = "countrySlug", source = "country.slug")
    @Mapping(target = "cityId", source = "id")
    @Mapping(target = "cityName", source = "name")
    @Mapping(target = "citySlug", source = "slug")
    @Mapping(target = "attractionId", ignore = true)
    @Mapping(target = "attractionSlug", ignore = true)
    SearchItemResponse fromCity(City city);

    @Mapping(target = "type", constant = "attraction")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "countryId", source = "city.country.id")
    @Mapping(target = "countryName", source = "city.country.name")
    @Mapping(target = "countrySlug", source = "city.country.slug")
    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "citySlug", source = "city.slug")
    @Mapping(target = "attractionId", source = "id")
    @Mapping(target = "attractionSlug", source = "slug")
    SearchItemResponse fromAttraction(Attraction attraction);
}
