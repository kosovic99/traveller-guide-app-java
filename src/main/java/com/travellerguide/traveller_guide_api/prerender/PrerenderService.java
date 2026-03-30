package com.travellerguide.traveller_guide_api.prerender;

import com.travellerguide.traveller_guide_api.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.category.CategoryRepository;
import com.travellerguide.traveller_guide_api.city.CityRepository;
import com.travellerguide.traveller_guide_api.country.CountryRepository;
import com.travellerguide.traveller_guide_api.model.Attraction;
import com.travellerguide.traveller_guide_api.model.Category;
import com.travellerguide.traveller_guide_api.model.City;
import com.travellerguide.traveller_guide_api.model.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrerenderService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final AttractionRepository attractionRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(cacheNames = "prerenderRoutes")
    public PrerenderRoutesResponse getAllRoutes() {
        List<Country> countries = countryRepository.findAllByOrderByNameAsc().stream()
                .filter(country -> StringUtils.hasText(country.getSlug()))
                .toList();

        List<City> cities = cityRepository.findAllByOrderByNameAsc().stream()
                .filter(city -> StringUtils.hasText(city.getSlug()))
                .toList();

        List<Attraction> attractions = attractionRepository.findAllByOrderByNameAsc().stream()
                .filter(attraction ->
                        StringUtils.hasText(attraction.getSlug())
                                && attraction.getCity() != null
                                && StringUtils.hasText(attraction.getCity().getSlug())
                )
                .toList();

        List<Category> categories = categoryRepository.findByPublicVisibleTrueOrderBySortOrderAscNameAsc().stream()
                .filter(category -> StringUtils.hasText(category.getSlug()))
                .toList();

        return PrerenderRoutesResponse.of(
                countries,
                cities,
                attractions,
                categories
        );
    }
}