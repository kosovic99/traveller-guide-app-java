package com.travellerguide.traveller_guide_api.application.search;

import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.search.SearchItemMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.search.SearchItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final AttractionRepository attractionRepository;
    private final SearchItemMapper searchItemMapper;

    @Cacheable(cacheNames = "searchAll")
    public List<SearchItemResponse> getAllSearchItems() {
        List<SearchItemResponse> items = new ArrayList<>();

        countryRepository.findAllByOrderByNameAsc().stream()
                .filter(country -> StringUtils.hasText(country.getSlug()))
                .map(searchItemMapper::fromCountry)
                .forEach(items::add);

        cityRepository.findAllByOrderByNameAsc().stream()
                .filter(city -> StringUtils.hasText(city.getSlug()))
                .map(searchItemMapper::fromCity)
                .forEach(items::add);

        attractionRepository.findAllByOrderByNameAsc().stream()
                .filter(attraction ->
                        StringUtils.hasText(attraction.getSlug())
                                && attraction.getCity() != null
                                && StringUtils.hasText(attraction.getCity().getSlug())
                )
                .map(searchItemMapper::fromAttraction)
                .forEach(items::add);

        items.sort(Comparator.comparing(
                SearchItemResponse::title,
                String.CASE_INSENSITIVE_ORDER
        ));

        return items;
    }
}

