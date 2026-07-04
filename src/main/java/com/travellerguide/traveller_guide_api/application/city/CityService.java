package com.travellerguide.traveller_guide_api.application.city;

import com.travellerguide.traveller_guide_api.application.i18n.SupportedLocale;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationFallbacks;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationView;
import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityTranslationRepository;
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
    private final CityTranslationRepository cityTranslationRepository;
    private final AttractionRepository attractionRepository;
    private final CityMapper cityMapper;

    @Cacheable(cacheNames = "cityBySlug", key = "#citySlug")
    public CityResponse getCityBySlug(String citySlug) {
        return getCityBySlug(SupportedLocale.DEFAULT, citySlug);
    }

    @Cacheable(cacheNames = "cityBySlug", key = "#locale + '::' + #citySlug")
    public CityResponse getCityBySlug(String locale, String citySlug) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        City city = findCityBySlugOrThrow(resolvedLocale, citySlug);
        CityTranslation translation = cityTranslationRepository
                .findByCity_IdAndLocale(city.getId(), resolvedLocale)
                .orElse(null);

        long attractionCount = attractionRepository.countByCity_Id(city.getId());

        return toLocalizedCityResponse(city, translation, resolvedLocale, attractionCount);
    }

    public CityResponse getCityBySlugLegacy(String citySlug) {
        City city = findCityBySlugOrThrow(citySlug);

        long attractionCount = attractionRepository.countByCity_Id(city.getId());

        return cityMapper.toResponse(city, attractionCount);
    }

    @Cacheable(cacheNames = "countryCities", key = "#citySlug + '::' + #sort")
    public List<CityResponse> getCountryCities(String citySlug, CitySort sort) {
        return getCountryCities(SupportedLocale.DEFAULT, citySlug, sort);
    }

    @Cacheable(cacheNames = "countryCities", key = "#locale + '::' + #citySlug + '::' + #sort")
    public List<CityResponse> getCountryCities(String locale, String citySlug, CitySort sort) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        City city = findCityBySlugOrThrow(resolvedLocale, citySlug);

        List<City> relatedCities = cityRepository.findByCountry_IdOrderByNameAsc(
                city.getCountry().getId()
        );

        return relatedCities.stream()
                .sorted(sort == null ? CitySort.NAME_ASC.comparator() : sort.comparator())
                .map(relatedCity -> {
                    CityTranslation translation = cityTranslationRepository
                            .findByCity_IdAndLocale(relatedCity.getId(), resolvedLocale)
                            .orElse(null);
                    return toLocalizedCityResponse(
                            relatedCity,
                            translation,
                            resolvedLocale,
                            attractionRepository.countByCity_Id(relatedCity.getId())
                    );
                })
                .toList();
    }

    public List<CityResponse> getCountryCitiesLegacy(String citySlug, CitySort sort) {
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

    private City findCityBySlugOrThrow(String locale, String citySlug) {
        if (!SupportedLocale.DEFAULT.equals(locale)) {
            return cityTranslationRepository.findByLocaleAndSlug(locale, citySlug)
                    .map(CityTranslation::getCity)
                    .orElseGet(() -> findCityBySlugOrThrow(citySlug));
        }

        return findCityBySlugOrThrow(citySlug);
    }

    private City findCityBySlugOrThrow(String citySlug) {
        return cityRepository.findFirstBySlugOrderByIdAsc(citySlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "City not found for slug: " + citySlug
                ));
    }

    private CityResponse toLocalizedCityResponse(
            City city,
            CityTranslation translation,
            String locale,
            Long attractionCount
    ) {
        TranslationView view = TranslationFallbacks.of(
                locale,
                translation != null ? translation.getName() : null,
                translation != null ? translation.getSlug() : null,
                translation != null ? translation.getDescription() : null,
                city.getName(),
                city.getSlug(),
                city.getDescription()
        );

        return new CityResponse(
                city.getId(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                view.name(),
                view.slug(),
                city.getFoto(),
                city.getRating(),
                view.description(),
                attractionCount,
                null,
                null
        );
    }
}

