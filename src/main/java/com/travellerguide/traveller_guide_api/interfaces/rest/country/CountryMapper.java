package com.travellerguide.traveller_guide_api.interfaces.rest.country;

import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    @Mapping(target = "id", source = "country.id")
    @Mapping(target = "name", source = "country.name")
    @Mapping(target = "slug", source = "country.slug")
    @Mapping(target = "foto", source = "country.foto")
    @Mapping(target = "flag", source = "country.flag")
    @Mapping(target = "description", source = "country.description")
    @Mapping(target = "cityCount", source = "cityCount")
    @Mapping(target = "attractionCount", source = "attractionCount")
    CountryResponse toResponse(Country country, Long cityCount, Long attractionCount);

    default CountryResponse toResponse(Country country) {
        return toResponse(country, null, null);
    }
}
