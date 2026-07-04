package com.travellerguide.traveller_guide_api.application.attraction;

import com.travellerguide.traveller_guide_api.application.i18n.SupportedLocale;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationFallbacks;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationView;
import com.travellerguide.traveller_guide_api.domain.attraction.AttractionTranslation;
import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryTranslationRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.attraction.AttractionMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.attraction.AttractionResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.city.City;
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
    private final AttractionTranslationRepository attractionTranslationRepository;
    private final CityRepository cityRepository;
    private final CityTranslationRepository cityTranslationRepository;
    private final CountryTranslationRepository countryTranslationRepository;
    private final AttractionMapper attractionMapper;
    private final CityMapper cityMapper;

    @Cacheable(cacheNames = "attractionsByCitySlug", key = "#citySlug")
    public CityResponse getCityWithAttractions(String citySlug) {
        return getCityWithAttractions(SupportedLocale.DEFAULT, citySlug);
    }

    @Cacheable(cacheNames = "attractionsByCitySlug", key = "#locale + '::' + #citySlug")
    public CityResponse getCityWithAttractions(String locale, String citySlug) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        City city = findCityBySlugOrThrow(resolvedLocale, citySlug);
        List<Attraction> attractions = attractionRepository.findByCity_IdOrderByNameAsc(city.getId());

        return toLocalizedCityWithAttractions(city, attractions, resolvedLocale);
    }

    public CityResponse getCityWithAttractionsLegacy(String citySlug) {
        City city = findCityBySlugOrThrow(citySlug);
        List<Attraction> attractions = attractionRepository.findByCity_IdOrderByNameAsc(city.getId());

        return cityMapper.toCityWithAttractions(city, attractions);
    }

    @Cacheable(cacheNames = "attractionByCityAndSlug", key = "#citySlug + '::' + #attractionSlug")
    public AttractionResponse getAttractionByCityAndSlug(String citySlug, String attractionSlug) {
        return getAttractionByCityAndSlug(SupportedLocale.DEFAULT, citySlug, attractionSlug);
    }

    @Cacheable(cacheNames = "attractionByCityAndSlug", key = "#locale + '::' + #citySlug + '::' + #attractionSlug")
    public AttractionResponse getAttractionByCityAndSlug(String locale, String citySlug, String attractionSlug) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        City city = findCityBySlugOrThrow(resolvedLocale, citySlug);
        Attraction attraction = findAttractionByCityAndSlugOrThrow(resolvedLocale, city, attractionSlug);

        return toLocalizedAttractionDetail(attraction, resolvedLocale);
    }

    public AttractionResponse getAttractionByCityAndSlugLegacy(String citySlug, String attractionSlug) {
        Attraction attraction = attractionRepository.findByCity_SlugAndSlug(citySlug, attractionSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attraction not found for city slug '" + citySlug + "' and attraction slug '" + attractionSlug + "'"
                ));

        return attractionMapper.toDetailResponse(attraction);
    }

    public boolean existsByCityAndSlug(String citySlug, String attractionSlug) {
        return existsByCityAndSlug(SupportedLocale.DEFAULT, citySlug, attractionSlug);
    }

    public boolean existsByCityAndSlug(String locale, String citySlug, String attractionSlug) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        try {
            City city = findCityBySlugOrThrow(resolvedLocale, citySlug);
            findAttractionByCityAndSlugOrThrow(resolvedLocale, city, attractionSlug);
            return true;
        } catch (ResourceNotFoundException ex) {
            return false;
        }
    }

    public boolean existsByCityAndSlugLegacy(String citySlug, String attractionSlug) {
        return attractionRepository.existsByCity_SlugAndSlug(citySlug, attractionSlug);
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

    private Attraction findAttractionByCityAndSlugOrThrow(String locale, City city, String attractionSlug) {
        if (!SupportedLocale.DEFAULT.equals(locale)) {
            return attractionTranslationRepository
                    .findByLocaleAndAttraction_City_IdAndSlug(locale, city.getId(), attractionSlug)
                    .map(AttractionTranslation::getAttraction)
                    .orElseGet(() -> findBaseAttractionByCityAndSlugOrThrow(city.getSlug(), attractionSlug));
        }

        return findBaseAttractionByCityAndSlugOrThrow(city.getSlug(), attractionSlug);
    }

    private Attraction findBaseAttractionByCityAndSlugOrThrow(String citySlug, String attractionSlug) {
        return attractionRepository.findByCity_SlugAndSlug(citySlug, attractionSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attraction not found for city slug '" + citySlug + "' and attraction slug '" + attractionSlug + "'"
                ));
    }

    private CityResponse toLocalizedCityWithAttractions(City city, List<Attraction> attractions, String locale) {
        CityTranslation cityTranslation = cityTranslationRepository.findByCity_IdAndLocale(city.getId(), locale)
                .orElse(null);
        TranslationView cityView = TranslationFallbacks.of(
                locale,
                cityTranslation != null ? cityTranslation.getName() : null,
                cityTranslation != null ? cityTranslation.getSlug() : null,
                cityTranslation != null ? cityTranslation.getDescription() : null,
                city.getName(),
                city.getSlug(),
                city.getDescription()
        );

        Country country = city.getCountry();
        TranslationView countryView = country == null
                ? null
                : countryTranslationRepository.findByCountry_IdAndLocale(country.getId(), locale)
                .map(translation -> TranslationFallbacks.of(
                        locale,
                        translation.getName(),
                        translation.getSlug(),
                        translation.getDescription(),
                        country.getName(),
                        country.getSlug(),
                        country.getDescription()
                ))
                .orElseGet(() -> TranslationFallbacks.of(
                        locale,
                        null,
                        null,
                        null,
                        country.getName(),
                        country.getSlug(),
                        country.getDescription()
                ));

        List<CityResponse.AttractionItem> attractionItems = attractions.stream()
                .map(attraction -> toLocalizedAttractionItem(attraction, locale))
                .toList();

        CityResponse.CountryInfo countryInfo = country == null
                ? null
                : new CityResponse.CountryInfo(country.getId(), countryView.slug(), countryView.name());

        return new CityResponse(
                city.getId(),
                country != null ? country.getId() : null,
                cityView.name(),
                cityView.slug(),
                city.getFoto(),
                city.getRating(),
                cityView.description(),
                null,
                countryInfo,
                attractionItems
        );
    }

    private CityResponse.AttractionItem toLocalizedAttractionItem(Attraction attraction, String locale) {
        AttractionTranslation translation = attractionTranslationRepository
                .findByAttraction_IdAndLocale(attraction.getId(), locale)
                .orElse(null);
        TranslationView view = TranslationFallbacks.of(
                locale,
                translation != null ? translation.getName() : null,
                translation != null ? translation.getSlug() : null,
                translation != null ? translation.getDescription() : null,
                attraction.getName(),
                attraction.getSlug(),
                attraction.getDescription()
        );

        return new CityResponse.AttractionItem(
                attraction.getId(),
                attraction.getCity() != null ? attraction.getCity().getId() : null,
                view.name(),
                view.slug(),
                view.description(),
                attraction.getFoto()
        );
    }

    private AttractionResponse toLocalizedAttractionDetail(Attraction attraction, String locale) {
        AttractionTranslation attractionTranslation = attractionTranslationRepository
                .findByAttraction_IdAndLocale(attraction.getId(), locale)
                .orElse(null);
        TranslationView attractionView = TranslationFallbacks.of(
                locale,
                attractionTranslation != null ? attractionTranslation.getName() : null,
                attractionTranslation != null ? attractionTranslation.getSlug() : null,
                attractionTranslation != null ? attractionTranslation.getDescription() : null,
                attraction.getName(),
                attraction.getSlug(),
                attraction.getDescription()
        );

        City city = attraction.getCity();
        Country country = city != null ? city.getCountry() : null;
        TranslationView cityView = city == null
                ? null
                : cityTranslationRepository.findByCity_IdAndLocale(city.getId(), locale)
                .map(translation -> TranslationFallbacks.of(locale, translation.getName(), translation.getSlug(), translation.getDescription(), city.getName(), city.getSlug(), city.getDescription()))
                .orElseGet(() -> TranslationFallbacks.of(locale, null, null, null, city.getName(), city.getSlug(), city.getDescription()));
        TranslationView countryView = country == null
                ? null
                : countryTranslationRepository.findByCountry_IdAndLocale(country.getId(), locale)
                .map(translation -> TranslationFallbacks.of(locale, translation.getName(), translation.getSlug(), translation.getDescription(), country.getName(), country.getSlug(), country.getDescription()))
                .orElseGet(() -> TranslationFallbacks.of(locale, null, null, null, country.getName(), country.getSlug(), country.getDescription()));

        AttractionResponse.CountryInfo countryInfo = country == null
                ? null
                : new AttractionResponse.CountryInfo(country.getId(), countryView.slug(), countryView.name());
        AttractionResponse.CityInfo cityInfo = city == null
                ? null
                : new AttractionResponse.CityInfo(
                city.getId(),
                cityView.slug(),
                cityView.name(),
                country != null ? country.getId() : null,
                countryInfo
        );

        return new AttractionResponse(
                attraction.getId(),
                city != null ? city.getId() : null,
                attractionView.name(),
                attractionView.slug(),
                attractionView.description(),
                attraction.getFoto(),
                cityInfo
        );
    }
}

