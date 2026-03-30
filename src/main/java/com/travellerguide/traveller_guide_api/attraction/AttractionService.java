package com.travellerguide.traveller_guide_api.attraction;

import com.travellerguide.traveller_guide_api.city.CityRepository;
import com.travellerguide.traveller_guide_api.city.CityResponse;
import com.travellerguide.traveller_guide_api.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.model.Attraction;
import com.travellerguide.traveller_guide_api.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final CityRepository cityRepository;

    @Cacheable(cacheNames = "attractionsByCitySlug", key = "#citySlug")
    public CityResponse getCityWithAttractions(String citySlug) {
        City city = findCityBySlugOrThrow(citySlug);
        List<Attraction> attractions = attractionRepository.findByCity_IdOrderByNameAsc(city.getId());

        return CityResponse.fromCityWithAttractions(city, attractions);
    }

    @Cacheable(cacheNames = "attractionByCityAndSlug", key = "#citySlug + '::' + #attractionSlug")
    public AttractionResponse getAttractionByCityAndSlug(String citySlug, String attractionSlug) {
        Attraction attraction = attractionRepository.findByCity_SlugAndSlug(citySlug, attractionSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attraction not found for city slug '" + citySlug + "' and attraction slug '" + attractionSlug + "'"
                ));

        return AttractionResponse.fromDetail(attraction);
    }

    public boolean existsByCityAndSlug(String citySlug, String attractionSlug) {
        return attractionRepository.existsByCity_SlugAndSlug(citySlug, attractionSlug);
    }

    private City findCityBySlugOrThrow(String citySlug) {
        return cityRepository.findFirstBySlugOrderByIdAsc(citySlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "City not found for slug: " + citySlug
                ));
    }
}