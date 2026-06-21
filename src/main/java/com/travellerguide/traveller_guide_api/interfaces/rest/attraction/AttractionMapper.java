package com.travellerguide.traveller_guide_api.interfaces.rest.attraction;

import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttractionMapper {

    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "city", ignore = true)
    AttractionResponse toResponse(Attraction attraction);

    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "city", source = "city")
    AttractionResponse toDetailResponse(Attraction attraction);

    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "country", source = "country")
    AttractionResponse.CityInfo toCityInfo(City city);

    AttractionResponse.CountryInfo toCountryInfo(Country country);
}
