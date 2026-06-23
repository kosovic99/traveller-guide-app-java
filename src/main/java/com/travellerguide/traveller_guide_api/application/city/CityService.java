package com.travellerguide.traveller_guide_api.application.city;

import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.domain.city.City;
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
    private final CityMapper cityMapper;

    @Cacheable(cacheNames = "cityBySlug", key = "#citySlug")
    public CityResponse getCityBySlug(String citySlug) {
        City city = findCityBySlugOrThrow(citySlug);

        long attractionCount = attractionRepository.countByCity_Id(city.getId());

        return cityMapper.toResponse(city, attractionCount);
    }

    @Cacheable(cacheNames = "countryCities", key = "#citySlug + '::' + #sort")
    public List<CityResponse> getCountryCities(String citySlug, CitySort sort) {
        City city = findCityBySlugOrThrow(citySlug);

        List<City> relatedCities = cityRepository.findByCountry_IdOrderByNameAsc(
                city.getCountry().getId()
        );

        return relatedCities.stream()
                .sorted(sort == null ? CitySort.NAME_ASC.comparator() : sort.comparator())
                .map(relatedCity -> cityMapper.toResponse(
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

