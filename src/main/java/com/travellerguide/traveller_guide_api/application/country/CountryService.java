package com.travellerguide.traveller_guide_api.application.country;

import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.application.city.CitySort;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.country.CountryMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.country.CountryResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
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
    private final CountryMapper countryMapper;
    private final CityMapper cityMapper;

    @Cacheable(cacheNames = "countries")
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(country -> countryMapper.toResponse(
                        country,
                        cityRepository.countByCountry_Id(country.getId()),
                        attractionRepository.countByCity_Country_Id(country.getId())
                ))
                .toList();
    }

    @Cacheable(cacheNames = "countryBySlug", key = "#slug")
    public CountryResponse getCountryBySlug(String slug) {
        Country country = findCountryBySlugOrThrow(slug);
        return countryMapper.toResponse(country);
    }

    @Cacheable(cacheNames = "citiesByCountrySlug", key = "#countrySlug + '::' + #sort")
    public List<CityResponse> getCitiesByCountrySlug(String countrySlug, CitySort sort) {
        Country country = findCountryBySlugOrThrow(countrySlug);

        List<City> cities = cityRepository.findByCountry_IdOrderByNameAsc(country.getId());

        return cities.stream()
                .sorted(sort == null ? CitySort.RECOMMENDED.comparator() : sort.comparator())
                .map(city -> cityMapper.toResponse(
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

