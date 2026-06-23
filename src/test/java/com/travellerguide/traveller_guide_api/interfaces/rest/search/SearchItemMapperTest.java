package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchItemMapperTest {

    private final SearchItemMapper mapper = new SearchItemMapper();

    @Test
    void mapsCityToMinimalNavigationSearchItem() {
        Country country = country("Austria", "austria");
        City city = city("Vienna", "vienna", country);

        SearchItemResponse response = mapper.fromCity(city);

        assertThat(response.type()).isEqualTo("city");
        assertThat(response.title()).isEqualTo("Vienna");
        assertThat(response.subtitle()).isEqualTo("Austria");
        assertThat(response.score()).isZero();
        assertThat(response.countrySlug()).isEqualTo("austria");
        assertThat(response.citySlug()).isEqualTo("vienna");
        assertThat(response.attractionSlug()).isNull();
    }

    @Test
    void mapsAttractionWithCityAndCountryRouteContext() {
        Country country = country("Austria", "austria");
        City city = city("Vienna", "vienna", country);
        Attraction attraction = new Attraction();
        attraction.setName("Schonbrunn Palace");
        attraction.setSlug("schonbrunn-palace");
        attraction.setCity(city);

        SearchItemResponse response = mapper.fromAttraction(attraction);

        assertThat(response.type()).isEqualTo("attraction");
        assertThat(response.title()).isEqualTo("Schonbrunn Palace");
        assertThat(response.subtitle()).isEqualTo("Vienna");
        assertThat(response.countrySlug()).isEqualTo("austria");
        assertThat(response.citySlug()).isEqualTo("vienna");
        assertThat(response.attractionSlug()).isEqualTo("schonbrunn-palace");
    }

    private static Country country(String name, String slug) {
        Country country = new Country();
        country.setName(name);
        country.setSlug(slug);
        return country;
    }

    private static City city(String name, String slug, Country country) {
        City city = new City();
        city.setName(name);
        city.setSlug(slug);
        city.setCountry(country);
        return city;
    }
}
