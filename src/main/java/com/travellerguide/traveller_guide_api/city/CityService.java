package com.travellerguide.traveller_guide_api.city;

import com.travellerguide.traveller_guide_api.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;
    private final AttractionRepository attractionRepository;

    @Cacheable(cacheNames = "cityBySlug", key = "#citySlug")
    public CityResponse getCityBySlug(String citySlug) {
        City city = findCityBySlugOrThrow(citySlug);

        long attractionCount = attractionRepository.countByCity_Id(city.getId());

        return CityResponse.fromEntity(city, attractionCount);
    }

    @Cacheable(cacheNames = "countryCities", key = "#citySlug")
    public List<CityResponse> getCountryCities(String citySlug) {
        City city = findCityBySlugOrThrow(citySlug);

        List<City> relatedCities = cityRepository.findByCountry_IdOrderByNameAsc(
                city.getCountry().getId()
        );

        return relatedCities.stream()
                .map(relatedCity -> CityResponse.fromEntity(
                        relatedCity,
                        attractionRepository.countByCity_Id(relatedCity.getId())
                ))
                .toList();
    }

    private City findCityBySlugOrThrow(String citySlug) {
        return cityRepository.findFirstBySlugOrderByIdAsc(citySlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "City not found for slug: " + citySlug
                ));
    }
}