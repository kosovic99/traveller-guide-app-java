package com.travellerguide.traveller_guide_api.interfaces.rest.city;

import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CityMapper {

    @Mapping(target = "countryId", source = "city.country.id")
    @Mapping(target = "attractionCount", source = "attractionCount")
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "attractions", ignore = true)
    CityResponse toResponse(City city, Long attractionCount);

    @Mapping(target = "countryId", source = "city.country.id")
    @Mapping(target = "attractionCount", ignore = true)
    @Mapping(target = "country", source = "city.country")
    @Mapping(target = "attractions", source = "attractions")
    CityResponse toCityWithAttractions(City city, List<Attraction> attractions);

    CityResponse.CountryInfo toCountryInfo(Country country);

    @Mapping(target = "cityId", source = "city.id")
    CityResponse.AttractionItem toAttractionItem(Attraction attraction);

    default CityResponse toResponse(City city) {
        return toResponse(city, null);
    }
}
