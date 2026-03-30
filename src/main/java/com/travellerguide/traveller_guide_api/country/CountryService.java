package com.travellerguide.traveller_guide_api.country;

import com.travellerguide.traveller_guide_api.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.city.CityRepository;
import com.travellerguide.traveller_guide_api.city.CityResponse;
import com.travellerguide.traveller_guide_api.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.model.City;
import com.travellerguide.traveller_guide_api.model.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final AttractionRepository attractionRepository;

    @Cacheable(cacheNames = "countries")
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(country -> CountryResponse.fromEntity(
                        country,
                        cityRepository.countByCountry_Id(country.getId()),
                        attractionRepository.countByCity_Country_Id(country.getId())
                ))
                .toList();
    }

    @Cacheable(cacheNames = "countryBySlug", key = "#slug")
    public CountryResponse getCountryBySlug(String slug) {
        Country country = findCountryBySlugOrThrow(slug);
        return CountryResponse.fromEntity(country);
    }

    @Cacheable(cacheNames = "citiesByCountrySlug", key = "#countrySlug")
    public List<CityResponse> getCitiesByCountrySlug(String countrySlug) {
        Country country = findCountryBySlugOrThrow(countrySlug);

        List<City> cities = cityRepository.findByCountry_IdOrderByNameAsc(country.getId());

        return cities.stream()
                .map(city -> CityResponse.fromEntity(
                        city,
                        attractionRepository.countByCity_Id(city.getId())
                ))
                .toList();
    }

    private Country findCountryBySlugOrThrow(String slug) {
        return countryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found for slug: " + slug
                ));
    }
}