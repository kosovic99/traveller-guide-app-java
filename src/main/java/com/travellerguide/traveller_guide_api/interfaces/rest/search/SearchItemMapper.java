package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.springframework.stereotype.Component;

@Component
public class SearchItemMapper {

    public SearchItemResponse fromCountry(Country country) {
        return new SearchItemResponse(
                "country",
                country.getName(),
                null,
                0,
                country.getName(),
                country.getSlug(),
                null,
                null,
                null
        );
    }

    public SearchItemResponse fromCity(City city) {
        Country country = city.getCountry();

        return new SearchItemResponse(
                "city",
                city.getName(),
                country != null ? country.getName() : null,
                0,
                country != null ? country.getName() : null,
                country != null ? country.getSlug() : null,
                city.getName(),
                city.getSlug(),
                null
        );
    }

    public SearchItemResponse fromAttraction(Attraction attraction) {
        City city = attraction.getCity();
        Country country = city != null ? city.getCountry() : null;

        return new SearchItemResponse(
                "attraction",
                attraction.getName(),
                city != null ? city.getName() : null,
                0,
                country != null ? country.getName() : null,
                country != null ? country.getSlug() : null,
                city != null ? city.getName() : null,
                city != null ? city.getSlug() : null,
                attraction.getSlug()
        );
    }
}
